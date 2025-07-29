package com.konnipativenkatesh7.queensrush

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konnipativenkatesh7.queensrush.ui.theme.QueensRushTheme

class GameOverScene : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val score = intent.getIntExtra("score", 0)
        soundManager = SoundManager(this)
        
        // Handle back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                soundManager.playButtonClick()
                val intent = Intent(this@GameOverScene, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        })
        
        setContent {
            QueensRushTheme {
                val soundManagerState = remember { soundManager }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Text(
                            text = "Game Over!",
                            fontSize = 32.sp,
                            color = Color(0xFF00FFFF)
                        )
                        
                        Text(
                            text = "Score: $score",
                            fontSize = 24.sp,
                            color = Color(0xFF00FFFF)
                        )
                        
                        Button(
                            onClick = {
                                soundManagerState.playButtonClick()
                                val intent = Intent(this@GameOverScene, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FFFF),
                                        Color(0xFF40E0E0),
                                        Color(0xFF00FFFF)
                                    )
                                )
                            )
                        ) {
                            Text(
                                text = "Play Again",
                                color = Color(0xFF00FFFF)
                            )
                        }
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up sound manager resources
        soundManager.release()
    }
} 