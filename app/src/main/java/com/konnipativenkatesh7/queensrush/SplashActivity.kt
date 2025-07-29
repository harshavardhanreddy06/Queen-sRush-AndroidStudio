package com.konnipativenkatesh7.queensrush

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var pinkQueen: ImageView
    private lateinit var blueQueen: ImageView
    private lateinit var titleText: TextView
    private val splashTimeOut: Long = 3000 // 3 seconds
    private var progress = 0
    private val handler = Handler(Looper.getMainLooper())
    private var animatorSet: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initializeViews()
        startAnimations()
        startProgressAnimation()

        // Navigate to MainActivity after splash timeout
        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashTimeOut)
    }

    private fun initializeViews() {
        progressBar = findViewById(R.id.progressBar)
        pinkQueen = findViewById(R.id.ivQueenPink)
        blueQueen = findViewById(R.id.ivQueenBlue)
        titleText = findViewById(R.id.tvTitle)
        progressBar.max = 100
    }

    private fun startAnimations() {
        // Create animations for the queens
        val pinkQueenAnim = ObjectAnimator.ofFloat(pinkQueen, "translationX", -200f, 0f)
        val blueQueenAnim = ObjectAnimator.ofFloat(blueQueen, "translationX", 200f, 0f)
        val titleAnim = ObjectAnimator.ofFloat(titleText, "alpha", 0f, 1f)

        // Configure animations
        pinkQueenAnim.duration = 1000
        blueQueenAnim.duration = 1000
        titleAnim.duration = 1500

        // Use AccelerateDecelerateInterpolator for smooth animation
        val interpolator = AccelerateDecelerateInterpolator()
        pinkQueenAnim.interpolator = interpolator
        blueQueenAnim.interpolator = interpolator

        // Combine animations
        animatorSet = AnimatorSet().apply {
            playTogether(pinkQueenAnim, blueQueenAnim, titleAnim)
            start()
        }
    }

    private fun startProgressAnimation() {
        val progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        progressAnimator.duration = splashTimeOut
        progressAnimator.interpolator = AccelerateDecelerateInterpolator()
        progressAnimator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        animatorSet?.cancel()
        handler.removeCallbacksAndMessages(null)
    }
}