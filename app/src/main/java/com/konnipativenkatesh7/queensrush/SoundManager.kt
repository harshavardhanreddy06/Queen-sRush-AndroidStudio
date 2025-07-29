package com.konnipativenkatesh7.queensrush

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf

class SoundManager(private val context: Context) {
    private var backgroundMusic: MediaPlayer? = null
    private var buttonClickSound: MediaPlayer? = null
    private var gameOverSound: MediaPlayer? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    
    var isMusicMuted = false
        private set
    
    var isSoundMuted = false
        private set
    
    var isVibrationEnabled = true
        private set

    private val vibrator = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    } catch (e: Exception) {
        Log.e("SoundManager", "Error initializing vibrator: ${e.message}")
        null
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pauseBackgroundMusic()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                resumeBackgroundMusic()
            }
        }
    }

    init {
        try {
            initializeSounds()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing sounds: ${e.message}")
        }
    }

    private fun initializeSounds() {
        // Initialize button click sound
        val buttonClickId = context.resources.getIdentifier("button_click", "raw", context.packageName)
        if (buttonClickId != 0) {
            buttonClickSound = MediaPlayer.create(context, buttonClickId)
        }

        // Initialize game over sound
        val gameOverId = context.resources.getIdentifier("game_over", "raw", context.packageName)
        if (gameOverId != 0) {
            gameOverSound = MediaPlayer.create(context, gameOverId)
        }
    }

    fun playBackgroundMusic() {
        try {
            if (!isMusicMuted && backgroundMusic == null && requestAudioFocus()) {
                val resourceId = context.resources.getIdentifier("background_music", "raw", context.packageName)
                if (resourceId != 0) {
                    backgroundMusic = MediaPlayer.create(context, resourceId).apply {
                        isLooping = true
                        start()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing background music: ${e.message}")
        }
    }

    fun pauseBackgroundMusic() {
        try {
            backgroundMusic?.pause()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error pausing music: ${e.message}")
        }
    }

    fun resumeBackgroundMusic() {
        if (!isMusicMuted && backgroundMusic?.isPlaying == false) {
            try {
                backgroundMusic?.start()
            } catch (e: Exception) {
                Log.e("SoundManager", "Error resuming music: ${e.message}")
            }
        }
    }

    fun playButtonClick() {
        if (!isSoundMuted) {
            try {
                buttonClickSound?.start()
            } catch (e: Exception) {
                Log.e("SoundManager", "Error playing button click: ${e.message}")
            }
        }
        if (isVibrationEnabled) {
            vibrate()
        }
    }

    fun playGameOver() {
        if (!isSoundMuted) {
            try {
                gameOverSound?.start()
            } catch (e: Exception) {
                Log.e("SoundManager", "Error playing game over sound: ${e.message}")
            }
        }
        if (isVibrationEnabled) {
            vibrate(duration = 500)
        }
    }

    fun toggleMusic() {
        isMusicMuted = !isMusicMuted
        if (isMusicMuted) {
            stopBackgroundMusic()
        } else {
            playBackgroundMusic()
        }
    }

    fun toggleSound() {
        isSoundMuted = !isSoundMuted
    }

    fun toggleVibration() {
        isVibrationEnabled = !isVibrationEnabled
    }

    private fun vibrate(duration: Long = 50) {
        if (!isVibrationEnabled || vibrator == null) return
        
        try {
            // Check if device has vibrator
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            duration,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(duration)
                }
            } else {
                Log.w("SoundManager", "Device does not have vibrator capability")
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error during vibration: ${e.message}")
            isVibrationEnabled = false // Disable vibration if there's an error
        }
    }

    fun release() {
        try {
            abandonAudioFocus()
            backgroundMusic?.apply {
                if (isPlaying) stop()
                release()
            }
            buttonClickSound?.release()
            gameOverSound?.release()
            
            // Cancel any ongoing vibrations
            vibrator?.cancel()
            
            backgroundMusic = null
            buttonClickSound = null
            gameOverSound = null
        } catch (e: Exception) {
            Log.e("SoundManager", "Error releasing resources: ${e.message}")
        }
    }

    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attributes)
                .setOnAudioFocusChangeListener(afChangeListener)
                .build()

            audioManager.requestAudioFocus(audioFocusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(afChangeListener)
        }
    }

    private fun stopBackgroundMusic() {
        try {
            backgroundMusic?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            backgroundMusic = null
        } catch (e: Exception) {
            Log.e("SoundManager", "Error stopping background music: ${e.message}")
        }
    }

    fun testSounds() {
        Log.d("SoundManager", "Testing sounds (silent mode)...")
        Log.d("SoundManager", "isMusicMuted: $isMusicMuted")
        Log.d("SoundManager", "isSoundMuted: $isSoundMuted")
    }

    fun stopGameOverSound() {
        gameOverSound?.let { sound ->
            if (sound.isPlaying) {
                sound.stop()
                sound.prepare() // Reset the media player for future use
            }
        }
    }
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    return soundManager
} 