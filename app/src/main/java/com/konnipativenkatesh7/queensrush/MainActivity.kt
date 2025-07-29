package com.konnipativenkatesh7.queensrush

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.konnipativenkatesh7.queensrush.ui.theme.QueensRushTheme
import kotlinx.coroutines.*
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.semantics.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.error
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


private val iconSize = 48.dp

private fun Modifier.iconSize(size: Dp) = this.size(size)

@Composable
fun GlowingIcon(
    imageVector: ImageVector,
    contentDescription: String
) {
    Box {
        // Outer glow effect
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .iconSize(42.dp)  // Slightly larger for glow effect
                .alpha(0.4f),  // Semi-transparent for glow
            tint = Color(0xFF00FFFF)  // Cyan color to match your theme
        )
        
        // Main icon
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.iconSize(38.dp),
            tint = Color(0xFF00FFFF)  // Cyan color to match your theme
        )
    }
}

@Composable
fun PvPIcon() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.width(60.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.player_icon),
            contentDescription = "Player 1",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.player_icon),
            contentDescription = "Player 2",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun PvBotIcon() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.width(60.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.player_icon),
            contentDescription = "Player",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_pvbot),
            contentDescription = "Bot",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF00FFFF)
        )
    }
}

@Composable
fun PlayerBox(
    playerName: String,
    isReversed: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                BorderStroke(
                    width = 3.dp,  // Increased from 2.dp
                    color = Color(0xFF00FFFF)  // Using solid cyan color
                ),
                shape = RoundedCornerShape(25.dp)  // Increased from 20.dp
            )
            .background(Color.Black, RoundedCornerShape(25.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = playerName,
            color = Color(0xFF00FFFF),
            fontSize = 28.sp,  // Increased from 20.sp
            fontWeight = FontWeight.Bold,
            modifier = Modifier.run {
                if (isReversed) this.rotate(180f) else this
            }
        )
    }
}

/**
 * Checks if two queens can attack each other on a chess board
 * @param row1 Row position of first queen
 * @param col1 Column position of first queen
 * @param row2 Row position of second queen
 * @param col2 Column position of second queen
 * @return true if queens can attack each other, false otherwise
 */
private fun isQueenAttacking(
    row1: Int, col1: Int,
    row2: Int, col2: Int
): Boolean {
    // Same row or column
    if (row1 == row2 || col1 == col2) return true
    
    // Check diagonals
    return Math.abs(row1 - row2) == Math.abs(col1 - col2)
}

private fun checkGameOver(queenPositions: Array<Array<Int>>): Boolean {
    val queenLocations = mutableListOf<Pair<Int, Int>>()
    
    // Collect all queen positions
    for (row in queenPositions.indices) {
        for (col in queenPositions[row].indices) {
            if (queenPositions[row][col] != 0) {
                queenLocations.add(row to col)
            }
        }
    }
    
    // Check each pair of queens
    for (i in queenLocations.indices) {
        for (j in i + 1 until queenLocations.size) {
            val (row1, col1) = queenLocations[i]
            val (row2, col2) = queenLocations[j]
            
            if (isQueenAttacking(row1, col1, row2, col2)) {
                return true
            }
        }
    }
    return false
}

// Update GameScene enum
enum class GameScene {
    MAIN_MENU,
    GAME_6X6,
    GAME_8X8,
    GAME_BOT_6X6,
    GAME_BOT_8X8,
    SETTINGS,
    INFO,
    GAME_OVER
}

// Add SplashScreen composable
@Composable
fun SplashScreen(onSplashScreenFinish: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000)
    )
    
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        onSplashScreenFinish()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Game Title
            Text(
                text = "Queen's Rush",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .alpha(alphaAnim.value)
                    .padding(bottom = 32.dp)
            )
            
            // Crown Logo
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .alpha(alphaAnim.value)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_compass),
                    contentDescription = "Crown Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// Add this data class at the top level
data class QueenColor(
    val iconResId: Int,
    val name: String
)

// Update the queen colors list with all available icons
private val queenColors = listOf(
    QueenColor(R.drawable.queen_icon_blue, "Blue"),
    QueenColor(R.drawable.queen_icon_pink, "Pink"),
    QueenColor(R.drawable.queen_icon_bright_red_orange, "RedOrange"),
    QueenColor(R.drawable.queen_icon_bright_skyblue, "BrightSkyblue"),
    QueenColor(R.drawable.queen_icon_deep_purple_gray, "PurpleGray"),
    QueenColor(R.drawable.queen_icon_yellow, "Yellow"),
    QueenColor(R.drawable.queen_icon_deep_teal_green, "Deeptealgreen"),
    QueenColor(R.drawable.queen_icon_dodger_blue, "Dodgerblue"),
    QueenColor(R.drawable.queen_icon_goldenrod_orange, "Goldenrodorange"),
    QueenColor(R.drawable.queen_icon_antique_white, "White"),
    QueenColor(R.drawable.queen_icon_light_mint_green, "Lightmintgreen"),
    QueenColor(R.drawable.queen_icon_pastel_gold, "pastalgold"),
    QueenColor(R.drawable.queen_icon_peru, "Peru"),
    QueenColor(R.drawable.queen_icon_royal_blue, "RoyalBlue"),
    QueenColor(R.drawable.queen_icon_gold, "Gold"),
    QueenColor(R.drawable.queen_icon_indigo, "Indigo"),
    QueenColor(R.drawable.queen_icon_maroon, "Maroon"),
    QueenColor(R.drawable.queen_icon_sienna, "Sienna"),
    QueenColor(R.drawable.queen_icon_peach_puff, "Peachpuff"),
    QueenColor(R.drawable.queen_icon_olive, "Olive"),
    QueenColor(R.drawable.queen_icon_vivid_indigo, "Vividindigo"),
    QueenColor(R.drawable.queen_icon_light_khaki, "LightKhaki"),
    QueenColor(R.drawable.queen_icon_vivid_orange, "VividOrange"),
    QueenColor(R.drawable.queen_icon_soft_coral_red, "Softcoralred")
)

@Composable
private fun ColorSelector(
    colors: List<QueenColor>,
    selectedIndex: Int,
    onColorSelected: (Int) -> Unit,
    label: String
) {
    var isHovered by remember { mutableStateOf(false) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { if (selectedIndex > 0) onColorSelected(selectedIndex - 1) },
                enabled = selectedIndex > 0,
                modifier = Modifier.semantics { 
                    contentDescription = "Previous $label"
                    role = Role.Button
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_triangle_left),
                    contentDescription = null,
                    tint = if (selectedIndex > 0) Color(0xFF00FFFF) else Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(80.dp)
                    .padding(horizontal = 16.dp)
                    .semantics(mergeDescendants = true) { 
                        contentDescription = "Selected ${colors[selectedIndex].name} Queen"
                        selected = true
                    },
                contentAlignment = Alignment.Center
            ) {
                val scale by animateFloatAsState(
                    targetValue = if (isHovered) 1.2f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Icon(
                    painter = painterResource(id = colors[selectedIndex].iconResId),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .scale(scale)
                        .size(60.dp)
                )
            }

            IconButton(
                onClick = { if (selectedIndex < colors.size - 1) onColorSelected(selectedIndex + 1) },
                enabled = selectedIndex < colors.size - 1,
                modifier = Modifier.semantics { 
                    contentDescription = "Next $label"
                    role = Role.Button
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_triangle_right),
                    contentDescription = null,
                    tint = if (selectedIndex < colors.size - 1) Color(0xFF00FFFF) else Color.Gray
                )
            }
        }

        // Color name display
        Text(
            text = colors[selectedIndex].name,
            color = Color(0xFF00FFFF),
            fontSize = 16.sp,
            modifier = Modifier.semantics { 
                contentDescription = "Selected color: ${colors[selectedIndex].name}"
            }
        )
    }
}

@Composable
fun ColorSelectionPopup(
    onDismiss: () -> Unit,
    onPlay: (QueenColor, QueenColor) -> Unit,
    isBot: Boolean
) {
    var player1ColorIndex by remember { mutableStateOf(0) }
    var player2ColorIndex by remember { mutableStateOf(1) }
    var visible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .semantics { 
                contentDescription = "Color Selection Screen"
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = scaleOut()
        ) {
            Column(
                modifier = Modifier
                    .width(340.dp)
                    .border(
                        BorderStroke(2.dp, Color(0xFF00FFFF)),
                        RoundedCornerShape(16.dp)
                    )
                    .background(Color.Black, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Select Color",
                    color = Color(0xFF00FFFF),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { 
                        heading()
                        contentDescription = "Color Selection Title" 
                    }
                )

                // Player 1 color selection
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBot) "Your Color" else "Player 1 Color",
                        color = Color(0xFF00FFFF),
                        fontSize = 20.sp,
                        modifier = Modifier.semantics { 
                            heading()
                            contentDescription = if (isBot) "Your Color Selection" else "Player 1 Color Selection"
                        }
                    )
                    
                    ColorSelector(
                        colors = queenColors,
                        selectedIndex = player1ColorIndex,
                        onColorSelected = { 
                            player1ColorIndex = it
                            showError = false
                        },
                        label = if (isBot) "Your Color" else "Player 1 Color"
                    )
                }

                // Player 2 color selection (only show in PvP mode)
                if (!isBot) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Player 2 Color",
                            color = Color(0xFF00FFFF),
                            fontSize = 20.sp,
                            modifier = Modifier.semantics { 
                                heading()
                                contentDescription = "Player 2 Color Selection"
                            }
                        )
                        
                        ColorSelector(
                            colors = queenColors,
                            selectedIndex = player2ColorIndex,
                            onColorSelected = { 
                                player2ColorIndex = it
                                showError = false
                            },
                            label = "Player 2 Color"
                        )
                    }
                }

                if (showError) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 16.sp,
                        modifier = Modifier.semantics { 
                            contentDescription = "Error: $errorMessage"
                            error(errorMessage)
                        }
                    )
                }

                // Buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Back button
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .width(100.dp)
                            .height(45.dp)
                            .semantics { 
                                contentDescription = "Back to Main Menu"
                                role = Role.Button
                            }
                    ) {
                        Text(
                            text = "Back",
                            color = Color(0xFF00FFFF),
                            fontSize = 18.sp
                        )
                    }

                    // Play button
                    Button(
                        onClick = {
                            onPlay(
                                queenColors[player1ColorIndex],
                                queenColors[player2ColorIndex]
                            )
                        },
                        enabled = player1ColorIndex != player2ColorIndex,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            disabledContainerColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .width(100.dp)
                            .height(45.dp)
                            .semantics { 
                                contentDescription = "Start Game with Selected Colors"
                                role = Role.Button
                            }
                    ) {
                        Text(
                            text = "Play",
                            color = Color(0xFF00FFFF),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// Update MainMenu to include the popup
@Composable
fun MainMenu(
    onGameTypeSelected: (GameScene, Int, QueenColor, QueenColor) -> Unit, // Modified signature
    onSettingsClick: () -> Unit
) {
    var selectedGridSize by remember { mutableStateOf(6) }
    var selectedTime by remember { mutableStateOf(30) }
    var showColorPopup by remember { mutableStateOf(false) }
    var pendingGameScene by remember { mutableStateOf<GameScene?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add initial spacing at the top
            Spacer(modifier = Modifier.weight(0.15f))  // Increased from 0.1f

            // Title and Settings Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Empty box for symmetry
                Box(modifier = Modifier.size(50.dp))

                // Title
                Text(
                    text = "Main Menu",
                    color = Color(0xFF00FFFF),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                // Settings button
                IconButton(
                    onClick = { onSettingsClick() },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(2.dp, Color(0xFF00FFFF)),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Spacing after title
            Spacer(modifier = Modifier.weight(0.1f))

            // Grid size selector section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Select Board",
                    color = Color(0xFF00FFFF),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left arrow
                    IconButton(
                        onClick = { if (selectedGridSize == 8) selectedGridSize = 6 },
                        enabled = selectedGridSize == 8,
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_triangle_left),
                            contentDescription = "Decrease grid size",
                            tint = if (selectedGridSize == 8) Color(0xFF00FFFF) else Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Grid preview
                    GridPreview(
                        gridSize = selectedGridSize,
                        modifier = Modifier
                            .size(200.dp)
                            .border(
                                BorderStroke(2.dp, Color(0xFF00FFFF)),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)
                    )

                    // Right arrow
                    IconButton(
                        onClick = { if (selectedGridSize == 6) selectedGridSize = 8 },
                        enabled = selectedGridSize == 6,
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_triangle_right),
                            contentDescription = "Increase grid size",
                            tint = if (selectedGridSize == 6) Color(0xFF00FFFF) else Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Text(
                    text = "${selectedGridSize}x${selectedGridSize}",
                    color = Color(0xFF00FFFF),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Spacing before time selector
            Spacer(modifier = Modifier.weight(0.1f))  // Added new spacer

            // Time selector section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(0.4f)  // Reduced from 0.5f
            ) {
                Text(
                    text = "Select Time",
                    color = Color(0xFF00FFFF),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left arrow
                    IconButton(
                        onClick = { if (selectedTime > 10) selectedTime -= 10 },
                        enabled = selectedTime > 10
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_triangle_left),
                            contentDescription = "Decrease time",
                            tint = if (selectedTime > 10) Color(0xFF00FFFF) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Time display
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(40.dp)
                            .border(
                                BorderStroke(2.dp, Color(0xFF00FFFF)),
                                RoundedCornerShape(8.dp)
                            )
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${selectedTime}s",
                            color = Color(0xFF00FFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Right arrow
                    IconButton(
                        onClick = { if (selectedTime < 60) selectedTime += 10 },
                        enabled = selectedTime < 60
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_triangle_right),
                            contentDescription = "Increase time",
                            tint = if (selectedTime < 60) Color(0xFF00FFFF) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Game mode buttons section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(0.8f)
            ) {
                Text(
                    text = "Select Mode",
                    color = Color(0xFF00FFFF),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Player vs Player button
                Button(
                    onClick = {
                        pendingGameScene = if (selectedGridSize == 6) GameScene.GAME_6X6 else GameScene.GAME_8X8
                        showColorPopup = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.player_icon),
                            contentDescription = "Player 1",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "VS",
                            color = Color(0xFF00FFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.player_icon),
                            contentDescription = "Player 2",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Player vs Bot button
                Button(
                    onClick = {
                        pendingGameScene = if (selectedGridSize == 6) GameScene.GAME_BOT_6X6 else GameScene.GAME_BOT_8X8
                        showColorPopup = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.player_icon),
                            contentDescription = "Player",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "VS",
                            color = Color(0xFF00FFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pvbot),
                            contentDescription = "Bot",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Bottom spacing
            Spacer(modifier = Modifier.weight(0.1f))
        }

        // Add the popup
        if (showColorPopup) {
            ColorSelectionPopup(
                onDismiss = { 
                    showColorPopup = false
                    pendingGameScene = null
                },
                onPlay = { color1, color2 ->
                    showColorPopup = false
                    pendingGameScene?.let { scene ->
                        onGameTypeSelected(scene, selectedTime, color1, color2)
                    }
                },
                isBot = pendingGameScene == GameScene.GAME_BOT_6X6 || pendingGameScene == GameScene.GAME_BOT_8X8
            )
        }
    }
}

@Composable
fun GridPreview(
    gridSize: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(200.dp)
            .border(
                BorderStroke(2.dp, Color(0xFF00FFFF)),
                RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
            .background(Color.Black)
    ) {
        with(LocalDensity.current) {
            // Calculate cell size
            val totalSize = 200.dp - 8.dp // Account for padding
            val cellSize = totalSize / gridSize
            val spacing = 2.dp
            val queenSize = cellSize * 0.8f

            // Create grid of cells
            for (row in 0 until gridSize) {
                for (col in 0 until gridSize) {
                    val isDark = (row + col) % 2 == 0
                    Box(
                        modifier = Modifier
                            .size(cellSize - spacing)
                            .offset(
                                x = cellSize * col,
                                y = cellSize * row
                            )
                            .background(
                                if (isDark) Color(0xFF1A1A1A) else Color(0xFF333333),
                                RoundedCornerShape(2.dp)
                            )
                            .border(
                                BorderStroke(1.dp, Color(0xFF00FFFF).copy(alpha = 0.3f)),
                                RoundedCornerShape(2.dp)
                            )
                    )

                    // Add sample queens for visual reference
                    if ((row == 1 && col == 1) || (row == gridSize - 2 && col == gridSize - 2)) {
                        Icon(
                            painter = painterResource(
                                id = if (row == 1) R.drawable.queen_icon_blue else R.drawable.queen_icon_pink
                            ),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(queenSize)
                                .offset(
                                    x = cellSize * col + (cellSize * 0.1f),
                                    y = cellSize * row + (cellSize * 0.1f)
                                )
                        )
                    }
                }
            }
        }
    }
}

// Add this composable near other UI components
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    soundManager: SoundManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .background(Color.Black)
                .border(
                    BorderStroke(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00FFFF),
                                Color(0xFF40E0E0),
                                Color(0xFF00FFFF)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFF00FFFF),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = message,
                color = Color(0xFF00FFFF),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // No Button
                Button(
                    onClick = { 
                        soundManager.playButtonClick()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color(0xFF00FFFF)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "No",
                        color = Color(0xFF00FFFF),
                        fontSize = 20.sp
                    )
                }

                // Yes Button
                Button(
                    onClick = { 
                        soundManager.playButtonClick()
                        onConfirm()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
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
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Yes",
                        color = Color(0xFF00FFFF),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

// Update SettingsPanel composable
@Composable
fun SettingsPanel(
    soundManager: SoundManager,
    onDismiss: () -> Unit,
    onInfoClick: () -> Unit
) {
    // Fix the state declarations by specifying the type
    var musicMuted by remember { mutableStateOf<Boolean>(soundManager.isMusicMuted) }
    var soundMuted by remember { mutableStateOf<Boolean>(soundManager.isSoundMuted) }
    var vibrationEnabled by remember { mutableStateOf<Boolean>(soundManager.isVibrationEnabled) }
    
    // Update all states when component is recomposed
    LaunchedEffect(Unit) {
        musicMuted = soundManager.isMusicMuted
        soundMuted = soundManager.isSoundMuted
        vibrationEnabled = soundManager.isVibrationEnabled
    }

    // Back button handling
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(backDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                soundManager.playButtonClick()
                onDismiss()
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    var showQuitConfirmation by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title with back button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        soundManager.playButtonClick()
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(2.dp, Color(0xFF00FFFF)),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Settings",
                    color = Color(0xFF00FFFF),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                // Empty box for symmetry
                Box(modifier = Modifier.size(50.dp))
            }

            // Settings options
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Sound settings section
                Text(
                    text = "Sound & Vibration",
                    color = Color(0xFF00FFFF),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Music Toggle
                Button(
                    onClick = { 
                        soundManager.playButtonClick()
                        musicMuted = !musicMuted  
                        soundManager.toggleMusic()  
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Music",
                            color = Color(0xFF00FFFF),
                            fontSize = 20.sp
                        )
                        Icon(
                            painter = painterResource(
                                id = if (musicMuted)
                                    R.drawable.ic_music_off
                                else 
                                    R.drawable.ic_music_on
                            ),
                            contentDescription = "Toggle Music",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Sound Effects Toggle
                Button(
                    onClick = { 
                        if (!soundMuted) soundManager.playButtonClick()
                        soundMuted = !soundMuted
                        soundManager.toggleSound()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Sound Effects",
                            color = Color(0xFF00FFFF),
                            fontSize = 20.sp
                        )
                        Icon(
                            painter = painterResource(
                                id = if (soundMuted)
                                    R.drawable.ic_sound_off
                                else 
                                    R.drawable.ic_sound_on
                            ),
                            contentDescription = "Toggle Sound Effects",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Vibration Toggle
                Button(
                    onClick = { 
                        if (!soundMuted) soundManager.playButtonClick()
                        vibrationEnabled = !vibrationEnabled
                        soundManager.toggleVibration()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Vibration",
                            color = Color(0xFF00FFFF),
                            fontSize = 20.sp
                        )
                        Icon(
                            painter = painterResource(
                                id = if (vibrationEnabled)
                                    R.drawable.ic_vibration_on
                                else 
                                    R.drawable.ic_vibration_off
                            ),
                            contentDescription = "Toggle Vibration",
                            tint = Color(0xFF00FFFF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Other options section
                Text(
                    text = "Other Options",
                    color = Color(0xFF00FFFF),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Game Info Button
                Button(
                    onClick = { 
                        soundManager.playButtonClick()
                        onInfoClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Game Info",
                        color = Color(0xFF00FFFF),
                        fontSize = 20.sp
                    )
                }

                // Quit Button
                Button(
                    onClick = { 
                        soundManager.playButtonClick()
                        showQuitConfirmation = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Quit Game",
                        color = Color(0xFF00FFFF),
                        fontSize = 20.sp
                    )
                }
            }

            // Bottom spacer
            Spacer(modifier = Modifier.height(64.dp))
        }

        if (showQuitConfirmation) {
            ConfirmationDialog(
                title = "Quit Game",
                message = "Are you sure you want to quit?",
                onDismiss = { showQuitConfirmation = false },
                onConfirm = { android.os.Process.killProcess(android.os.Process.myPid()) },
                soundManager = soundManager
            )
        }
    }
}

// Add bot move calculation function
private fun calculateBotMove(
    queenPositions: Array<Array<Int>>,
    gridSize: Int
): Pair<Int, Int> {
    val availablePositions = mutableListOf<Pair<Int, Int>>()
    
    // Collect all empty positions
    for (row in 0 until gridSize) {
        for (col in 0 until gridSize) {
            if (queenPositions[row][col] == 0) {
                availablePositions.add(row to col)
            }
        }
    }
    
    availablePositions.shuffle()
    
    for (pos in availablePositions) {
        val (row, col) = pos
        val tempPositions = queenPositions.map { it.clone() }.toTypedArray()
        tempPositions[row][col] = 2  // Changed from 1 to 2 to test bot's move
        
        if (!checkGameOver(tempPositions)) {
            return pos
        }
    }
    
    return availablePositions.firstOrNull() ?: (0 to 0)
}

// Add this composable function
@Composable
fun GameMenuPopup(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit,
    onSettings: () -> Unit,  // Settings callback instead of Quit
    soundManager: SoundManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(16.dp))
                .border(
                    BorderStroke(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00FFFF),
                                Color(0xFF40E0E0),
                                Color(0xFF00FFFF)
                            )
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
                .padding(32.dp)
        ) {
            Text(
                text = "Game Paused",
                color = Color(0xFF00FFFF),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Resume Button
                IconButton(
                    onClick = { 
                        soundManager.playButtonClick()
                        onResume()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FFFF),
                                        Color(0xFF40E0E0),
                                        Color(0xFF00FFFF)
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Resume",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Restart Button
                IconButton(
                    onClick = { 
                        soundManager.playButtonClick()
                        onRestart()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FFFF),
                                        Color(0xFF40E0E0),
                                        Color(0xFF00FFFF)
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Main Menu Button
                IconButton(
                    onClick = { 
                        soundManager.playButtonClick()
                        onMainMenu()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FFFF),
                                        Color(0xFF40E0E0),
                                        Color(0xFF00FFFF)
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Main Menu",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Settings Button (replaced Quit button)
                IconButton(
                    onClick = { 
                        soundManager.playButtonClick()
                        onSettings()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00FFFF),
                                        Color(0xFF40E0E0),
                                        Color(0xFF00FFFF)
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

// Modify GameBoard to show single player box for bot games
@Composable
fun GameBoard(
    gridSize: Int,
    selectedTime: Int,
    onBackPressed: () -> Unit,
    soundManager: SoundManager,
    onGameOver: (GameState) -> Unit,
    onSettingsClick: () -> Unit,  // Add this parameter
    player1Queen: QueenColor, // Add these parameters
    player2Queen: QueenColor,
    isBot: Boolean
) {
    var timeRemaining by remember { mutableStateOf(selectedTime) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }
    
    val initialQueens = if (gridSize == 6) 4 else 5
    
    var queenPositions by remember {
        mutableStateOf(Array(gridSize) { Array(gridSize) { 0 } }) // 0 = empty, 1 = player1, 2 = player2
    }
    
    val player1RemainingQueens = remember { mutableStateOf(initialQueens) }
    val player2RemainingQueens = remember { mutableStateOf(initialQueens) }
    var isGameOver by remember { mutableStateOf(false) }
    val currentPlayer = remember { mutableStateOf(1) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    var lastBotMove by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    
    // Modify hint related state variables
    var showHint by remember { mutableStateOf(false) }
    var hintPositions by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var isHintBlinking by remember { mutableStateOf(false) }
    
    // Add coroutine scope for hint timer
    val scope = rememberCoroutineScope()

    // Modify hint effect to auto-hide after 3 seconds
    LaunchedEffect(showHint) {
        if (showHint && currentPlayer.value == 1) {  // Only show hints during player's turn
            isHintBlinking = true
            delay(3000)
            showHint = false
            isHintBlinking = false
            hintPositions = emptyList()
        }
    }

    // Add hint calculation function
    fun calculateHints(): List<Pair<Int, Int>> {
        val hints = mutableListOf<Pair<Int, Int>>()
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                if (queenPositions[row][col] == 0) {
                    val tempPositions = queenPositions.map { it.clone() }.toTypedArray()
                    tempPositions[row][col] = 1
                    if (!checkGameOver(tempPositions)) {
                        hints.add(row to col)
                    }
                }
            }
        }
        return hints
    }

    // Modify bot move effect to store last move
    LaunchedEffect(currentPlayer.value) {
        if (isBot && currentPlayer.value == 2 && !isGameOver) {
            delay(1000)
            val (row, col) = calculateBotMove(queenPositions, gridSize)
            
            if (player2RemainingQueens.value > 0) {
                val newPositions = queenPositions.map { it.clone() }.toTypedArray()
                newPositions[row][col] = 2  // Changed from 1 to 2 since this is the bot's move
                queenPositions = newPositions
                lastBotMove = row to col
                
                if (checkGameOver(queenPositions)) {
                    isGameOver = true
                } else {
                    player2RemainingQueens.value--
                    currentPlayer.value = 1
                }
            }
        }
    }

    // Timer effect that handles both turn changes and pause/resume
    LaunchedEffect(currentPlayer.value, showMenu) {
        if (!showMenu && !isGameOver) {  // Only start timer if game is active
            isTimerRunning = true
            
            while (isTimerRunning && timeRemaining > 0) {
                delay(1000L)
                if (!showMenu && !isGameOver) {  // Check again in case menu was opened
                    timeRemaining--
                    
                    if (timeRemaining == 0) {
                        isGameOver = true
                        currentPlayer.value = if (currentPlayer.value == 1) 2 else 1
                        soundManager.playGameOver()
                    }
                }
            }
        } else {
            isTimerRunning = false  // Pause timer when menu is shown
        }
    }

    // Reset timer on new turn
    LaunchedEffect(currentPlayer.value) {
        if (!showMenu && !isGameOver) {  // Only reset if game is active
            timeRemaining = selectedTime
        }
    }

    // Update queen placement logic to stop timer when move is made
    fun placeQueen(row: Int, col: Int) {
        if (!isGameOver && queenPositions[row][col] == 0) {
            val currentPlayerQueens = if (currentPlayer.value == 1) 
                player1RemainingQueens else player2RemainingQueens
            
            if (currentPlayerQueens.value > 0) {
                // Clear hints when player makes a move
                if (showHint) {
                    showHint = false
                    isHintBlinking = false
                    hintPositions = emptyList()
                }

                val newPositions = queenPositions.map { it.clone() }.toTypedArray()
                newPositions[row][col] = currentPlayer.value
                queenPositions = newPositions
                
                if (checkGameOver(newPositions)) {
                    isGameOver = true
                    soundManager.playGameOver()
                } else {
                    currentPlayerQueens.value--
                    currentPlayer.value = if (currentPlayer.value == 1) 2 else 1
                }
            }
        }
    }

    // Handle system back button
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(backDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showMenu = true
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    // Update the game over effect
    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            // Stop background music and play game over sound
            soundManager.playGameOver()
        }
    }

    // Add reset game function
    fun resetGame() {
        soundManager.playButtonClick()
        soundManager.stopGameOverSound()
        queenPositions = Array(gridSize) { Array(gridSize) { 0 } }
        player1RemainingQueens.value = initialQueens
        player2RemainingQueens.value = initialQueens
        currentPlayer.value = 1
        isGameOver = false
        lastBotMove = null
        showHint = false
        hintPositions = emptyList()
        // Reset timer to initial value
        timeRemaining = selectedTime
        isTimerRunning = true
        soundManager.playBackgroundMusic()
    }

    // Add resume game function
    fun resumeGame() {
        showMenu = false
        isTimerRunning = true  // Timer will continue from current timeRemaining
        soundManager.playButtonClick()
    }

    // First, add the losingMove state at the top of GameBoard with other state variables
    var losingMove by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Add the LaunchedEffect to find the losing move when game is over
    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            // Find the last move that caused the game over
            for (row in queenPositions.indices) {
                for (col in queenPositions[row].indices) {
                    if (queenPositions[row][col] == currentPlayer.value) {
                        val tempPositions = queenPositions.map { it.clone() }.toTypedArray()
                        tempPositions[row][col] = 0
                        if (!checkGameOver(tempPositions)) {
                            losingMove = row to col
                            break
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modify top bar back button to show menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { 
                        soundManager.playButtonClick()
                        showMenu = true 
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(2.dp, Color(0xFF00FFFF)),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Game content in a Box
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                    contentAlignment = Alignment.Center
            ) {
                if (isBot) {
                    // Hero text player box
                    PlayerBox(
                        playerName = if (currentPlayer.value == 1) "You" else "Clara",
                        isReversed = false,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-16).dp)  // Using negative offset instead of padding to move it way up
                            .size(width = 216.dp, height = 60.dp)
                    )

                    // Queens counting box
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(62.dp)  // Increased from 44.dp to 62.dp (40% more)
                    ) {
                        // Queens counting box for Player 1
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 50.dp)
                                .border(
                                    BorderStroke(2.dp, Color(0xFF00FFFF)),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .background(Color.Black, RoundedCornerShape(25.dp))
                                .padding(vertical = 8.dp)  // Added vertical padding
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()  // Fill the box
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically  // Center vertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (currentPlayer.value == 1)  
                                            R.drawable.queen_icon_blue 
                                        else 
                                            R.drawable.queen_icon_pink
                                    ),
                                    contentDescription = "Queen",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .then(
                                            if (currentPlayer.value == 2) 
                                                Modifier.rotate(180f) 
                                            else 
                                                Modifier
                                        ),
                                    tint = Color.Unspecified
                                )
                                
                                Text(
                                    text = ": ${if (currentPlayer.value == 1) player1RemainingQueens.value else player2RemainingQueens.value}",
                                    color = Color(0xFF00FFFF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Helper function to determine timer color
                        fun getTimerColor(currentTime: Int, totalTime: Int): Color {
                            return when {
                                currentTime <= (totalTime * 0.35) -> Color.Red  // Last 35% - Red
                                else -> Color(0xFF00FF00)  // First 65% - Green
                            }
                        }

                        // Timer box for Player 1
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 50.dp)
                                .border(
                                    BorderStroke(
                                        width = 2.dp,
                                        color = if (currentPlayer.value == 1) 
                                            getTimerColor(timeRemaining, selectedTime)
                                        else 
                                            Color(0xFF00FFFF).copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .background(Color.Black, RoundedCornerShape(25.dp))
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Timer",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (currentPlayer.value == 1) 
                                        getTimerColor(timeRemaining, selectedTime)
                                    else 
                                        Color(0xFF00FFFF).copy(alpha = 0.3f)
                                )
                                
                                Text(
                                    text = ":",
                                    color = if (currentPlayer.value == 1) 
                                        getTimerColor(timeRemaining, selectedTime)
                                    else 
                                        Color(0xFF00FFFF).copy(alpha = 0.3f),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = if (currentPlayer.value == 1) "$timeRemaining" else "$selectedTime",
                                    color = if (currentPlayer.value == 1) 
                                        getTimerColor(timeRemaining, selectedTime)
                                    else 
                                        Color(0xFF00FFFF).copy(alpha = 0.3f),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // Original two-player layout
                    // Player 1 box (top)
                    PlayerBox(
                        playerName = "Player 1",
                        isReversed = false,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 16.dp)  // Increased to move down a bit more
                            .size(width = 216.dp, height = 55.dp)
                            .border(
                                BorderStroke(
                                    width = 3.dp,
                                    color = Color(0xFF00FFFF)
                                ),
                                shape = RoundedCornerShape(25.dp)
                            )
                            .background(Color.Black, RoundedCornerShape(25.dp))
                    )

                    // Player 1 info boxes row
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 102.dp),  // Increased from 85.dp by 20% to bring closer to grid
                        horizontalArrangement = Arrangement.spacedBy(68.dp)  // Increased from 57.dp by 20%
                    ) {
                        // Queens counting box for Player 1
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 50.dp)
                                .border(
                                    BorderStroke(2.dp, Color(0xFF00FFFF)),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .background(Color.Black, RoundedCornerShape(25.dp))
                                .padding(vertical = 8.dp)  // Added vertical padding
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()  // Fill the box
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically  // Center vertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.queen_icon_blue),
                                    contentDescription = "Blue Queen",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.Unspecified
                                )
                                
                                Text(
                                    text = ":",
                                    color = Color(0xFF00FFFF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "${player1RemainingQueens.value}",
                                    color = Color(0xFF00FFFF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Timer box for Player 1
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 50.dp)
                                .border(
                                    BorderStroke(
                                        width = 2.dp,
                                        color = if (currentPlayer.value == 1) 
                                            getTimerColor(timeRemaining, selectedTime)
                                        else 
                                            Color(0xFF00FFFF).copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .background(Color.Black, RoundedCornerShape(25.dp))
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Timer",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (currentPlayer.value == 1) 
                                        getTimerColor(timeRemaining, selectedTime)
                                    else 
                                        Color(0xFF00FFFF).copy(alpha = 0.3f)
                                )
                                
                                Text(
                                    text = ":",
                                    color = if (currentPlayer.value == 1) 
                                        getTimerColor(timeRemaining, selectedTime)
                                    else 
                                        Color(0xFF00FFFF).copy(alpha = 0.3f),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = if (currentPlayer.value == 1) "$timeRemaining" else "$selectedTime",
                                    color = if (currentPlayer.value == 1) 
                                        getTimerColor(timeRemaining, selectedTime)
                                    else 
                                        Color(0xFF00FFFF).copy(alpha = 0.3f),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Player 2 box (bottom)
                    PlayerBox(
                        playerName = "Player 2",
                        isReversed = true,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-16).dp)  // Increased to move up a bit more
                            .size(width = 216.dp, height = 55.dp)
                            .border(
                                BorderStroke(
                                    width = 3.dp,
                                    color = Color(0xFF00FFFF)
                                ),
                                shape = RoundedCornerShape(25.dp)
                            )
                            .background(Color.Black, RoundedCornerShape(25.dp))
                    )

                    // Player 2 info boxes row
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 102.dp),  // Increased from 85.dp by 20% to bring closer to grid
                        horizontalArrangement = Arrangement.spacedBy(68.dp)  // Increased from 57.dp by 20%
                    ) {
                        // Queens counting box for Player 2
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 50.dp)
                                .border(
                                    BorderStroke(2.dp, Color(0xFF00FFFF)),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .background(Color.Black, RoundedCornerShape(25.dp))
                                .padding(vertical = 8.dp)  // Added vertical padding
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()  // Fill the box
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically  // Center vertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.queen_icon_pink),
                                    contentDescription = "Pink Queen",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .rotate(180f),
                                    tint = Color.Unspecified
                                )
                                
                                Text(
                                    text = ":",
                                    color = Color(0xFF00FFFF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.rotate(180f)
                                )
                                
                                Text(
                                    text = "${player2RemainingQueens.value}",
                                    color = Color(0xFF00FFFF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.rotate(180f)
                                )
                            }
                        }

                        // Timer box for Player 2
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 50.dp)
                                .border(
                                    BorderStroke(
                                        width = 2.dp,
                                        color = if (currentPlayer.value == 2) 
                                            getTimerColor(timeRemaining, selectedTime)
                                        else 
                                            Color(0xFF00FFFF).copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .background(Color.Black, RoundedCornerShape(25.dp))
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Timer",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .rotate(180f),
                                    tint = if (currentPlayer.value == 2) 
                                        getTimerColor(timeRemaining, selectedTime)
                                        else 
                                            Color(0xFF00FFFF).copy(alpha = 0.3f)
                                )
                                
                                Text(
                                    text = ":",
                                    color = if (currentPlayer.value == 2) 
                                        getTimerColor(timeRemaining, selectedTime)
                                        else 
                                            Color(0xFF00FFFF).copy(alpha = 0.3f),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.rotate(180f)
                                )
                                
                                Text(
                                    text = if (currentPlayer.value == 2) "$timeRemaining" else "$selectedTime",
                                    color = if (currentPlayer.value == 2) 
                                        getTimerColor(timeRemaining, selectedTime)
                                        else 
                                            Color(0xFF00FFFF).copy(alpha = 0.3f),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.rotate(180f)
                                )
                            }
                        }
                    }
                }

                // Game grid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer border (double cyan border)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth(0.8f)
                            .border(
                                BorderStroke(2.dp, Color(0xFF00FFFF)),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(2.dp)  // Small gap between double borders
                            .border(
                                BorderStroke(2.dp, Color(0xFF00FFFF)),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)  // Padding between border and grid
                            .background(Color.Black)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridSize),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),  // Gap between tiles
                            verticalArrangement = Arrangement.spacedBy(4.dp)     // Gap between tiles
                        ) {
                            items(gridSize * gridSize) { index ->
                                val row = index / gridSize
                                val col = index % gridSize
                                
                                val canPlaceQueen = remember(currentPlayer.value) {
                                    derivedStateOf {
                                        when (currentPlayer.value) {
                                            1 -> player1RemainingQueens.value > 0
                                            2 -> player2RemainingQueens.value > 0
                                            else -> false
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .background(
                                            Color(0xFF00FFFF).copy(alpha = 0.1f),  // Very light cyan background
                                            RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            BorderStroke(1.dp, Color(0xFF00FFFF).copy(alpha = 0.3f)),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable(
                                            enabled = !isGameOver && queenPositions[row][col] == 0 && canPlaceQueen.value
                                        ) {
                                            soundManager.playButtonClick()
                                            placeQueen(row, col)
                                        }
                                ) {
                                    if (queenPositions[row][col] != 0) {
                                        Icon(
                                            painter = painterResource(
                                                id = when (queenPositions[row][col]) {
                                                    1 -> player1Queen.iconResId
                                                    2 -> player2Queen.iconResId
                                                    else -> player1Queen.iconResId // fallback
                                                }
                                            ),
                                            contentDescription = when (queenPositions[row][col]) {
                                                1 -> player1Queen.name
                                                2 -> player2Queen.name
                                                else -> "Queen"
                                            },
                                            tint = Color.Unspecified,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(4.dp)
                                        )
                                    } else if (showHint && isHintBlinking && 
                                                hintPositions.contains(row to col) && 
                                                currentPlayer.value == 1) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.queen_icon_blue),
                                            contentDescription = "Blue Queen",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            tint = Color(0xFF00FF00)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Add game over popup directly in the game grid Box
                    if (isGameOver) {
                        var timeRemaining by remember { mutableStateOf(8) }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.94f)
                                .aspectRatio(1f)
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .border(
                                    BorderStroke(2.dp, Color(0xFF00FFFF)),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "Game Over",
                                    color = Color(0xFF00FFFF),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Skip button in center
                                Button(
                                    onClick = {
                                        onGameOver(GameState(
                                            winner = currentPlayer.value,
                                            isBot = isBot,
                                            player1Queens = player1RemainingQueens.value,
                                            player2Queens = player2RemainingQueens.value,
                                            gridSize = gridSize
                                        ))
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black,
                                        contentColor = Color(0xFF00FFFF)
                                    ),
                                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                                    shape = RoundedCornerShape(25.dp),
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(40.dp)
                                ) {
                                    Text(
                                        text = "Skip",
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            // Timer text in bottom right corner
                            Text(
                                text = "$timeRemaining seconds...",
                                color = Color(0xFF00FFFF),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(bottom = 8.dp, end = 8.dp)
                            )
                        }

                        // Countdown timer
                        LaunchedEffect(Unit) {
                            while (timeRemaining > 0) {
                                delay(1000)
                                timeRemaining--
                                if (timeRemaining == 0) {
                                    onGameOver(GameState(
                                        winner = currentPlayer.value,
                                        isBot = isBot,
                                        player1Queens = player1RemainingQueens.value,
                                        player2Queens = player2RemainingQueens.value,
                                        gridSize = gridSize
                                    ))
                                }
                            }
                        }
                    }
                }
            }

            // Error message
            errorMessage.value?.let { message ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    LaunchedEffect(message) {
                        delay(2000)
                        errorMessage.value = null
                    }
                }
            }

            // Add after the game grid but before game over message
            if (isBot) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -70.dp)  // Increased from -50.dp to -55.dp (10% closer)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(44.dp)  // Increased from 24.dp to 32.dp (about 30% more gap)
                    ) {
                        // Hint Button
                        IconButton(
                            onClick = {
                                if (!isGameOver && currentPlayer.value == 1 && !showHint) {
                                    showHint = true
                                    hintPositions = calculateHints()
                                }
                            },
                            enabled = !showHint && !isGameOver && currentPlayer.value == 1,
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color.Black)
                                .border(
                                    BorderStroke(2.dp, Color(0xFF00FFFF)),
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Hint",
                                tint = Color(0xFF00FFFF),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Revert Button
                        IconButton(
                            onClick = {
                                lastBotMove?.let { (row, col) ->
                                    if (!isGameOver) {
                                        // Remove bot's last move
                                        val newPositions = queenPositions.map { it.clone() }.toTypedArray()
                                        newPositions[row][col] = 0
                                        queenPositions = newPositions
                                        player2RemainingQueens.value++
                                        
                                        // Make bot place queen in a different position
                                        val (newRow, newCol) = calculateBotMove(newPositions, gridSize)
                                        newPositions[newRow][newCol] = 2
                                        queenPositions = newPositions
                                        lastBotMove = newRow to newCol
                                        
                                        if (checkGameOver(newPositions)) {
                                            isGameOver = true
                                        } else {
                                            // Reset player's timer since they get a fresh turn after using revert
                                            timeRemaining = selectedTime
                                            isTimerRunning = true
                                            currentPlayer.value = 1  // Ensure it's player's turn
                                        }
                                        
                                        // Here you would show the rewarded ad
                                        // After ad completes successfully, execute the above logic
                                    }
                                }
                            },
                            enabled = lastBotMove != null && !isGameOver,
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color.Black)
                                .border(
                                    BorderStroke(2.dp, Color(0xFF00FFFF)),
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Revert Bot Move",
                                tint = Color(0xFF00FFFF),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }

        // Show menu when back is pressed
        if (showMenu) {
            GameMenuPopup(
                onResume = { resumeGame() },
                onRestart = {
                    resetGame()
                    showMenu = false  // This will also restart the timer due to LaunchedEffect
                },
                onMainMenu = {
                    soundManager.playButtonClick()
                    onBackPressed()
                },
                onSettings = {
                    soundManager.playButtonClick()
                    onSettingsClick()  // Use the callback passed from parent
                },
                soundManager = soundManager
            )
        }
    }
}

@Composable
fun GameOverScreen(
    winner: Int,
    isBot: Boolean,
    player1Queens: Int,
    player2Queens: Int,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit,
    soundManager: SoundManager
) {
    // Handle back button
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(backDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                soundManager.playButtonClick()
                onMainMenu()
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    var showQuitConfirmation by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // Game Over Title with glowing effect
            Box(
                modifier = Modifier.padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main text (single layer)
                Text(
                    text = "Game Over",
                    color = Color(0xFF00FFFF),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Victory/Defeat Text with animation
            var startAnimation by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (startAnimation) 1.1f else 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )

            LaunchedEffect(Unit) {
                startAnimation = true
            }

            // Main victory/defeat message
            Text(
                text = when {
                    isBot && winner == 2 -> "Victory is Yours!" // Player won against Clara
                    isBot && winner == 1 -> "You Lost" // Clara won against player
                    else -> "Player $winner Wins!" // PvP mode
                },
                color = Color(0xFF00FFFF),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale)
            )

            // Subtitle message
            Text(
                text = when {
                    isBot && winner == 2 -> "A True Master of Strategy!" // Player victory message
                    isBot && winner == 1 -> "Defeat is Just the Start. Try Again, Stronger!" // Player defeat message
                    else -> "Hail the Ruler! A Kingdom Won by Queens!" // PvP victory message
                },
                color = Color(0xFF00FFFF),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                // Play Again Button
                GameOverButton(
                    icon = Icons.Default.Refresh,
                    label = "Play Again",
                    onClick = {
                        soundManager.playButtonClick()
                        onRestart()
                    }
                )

                // Main Menu Button
                GameOverButton(
                    icon = Icons.Default.Home,
                    label = "Main Menu",
                    onClick = {
                        soundManager.playButtonClick()
                        onMainMenu()
                    }
                )

                // Quit Button
                GameOverButton(
                    icon = Icons.Default.Close,
                    label = "Quit",
                    onClick = {
                        soundManager.playButtonClick()
                        showQuitConfirmation = true
                    }
                )
            }
        }

        // Quit confirmation dialog
        if (showQuitConfirmation) {
            ConfirmationDialog(
                title = "Quit Game",
                message = "Are you sure you want to quit?",
                onDismiss = { showQuitConfirmation = false },
                onConfirm = { android.os.Process.killProcess(android.os.Process.myPid()) },
                soundManager = soundManager
            )
        }
    }
}

@Composable
private fun ScoreColumn(
    label: String,
    score: Int,
    queenColor: QueenColor,  // Changed from icon: Int
    rotateIcon: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF00FFFF),
            fontSize = 24.sp
        )
        
        Icon(
            painter = painterResource(id = queenColor.iconResId),  // Use the queen color's icon
            contentDescription = queenColor.name,  // Use the queen color's name
            tint = Color.Unspecified,
            modifier = Modifier
                .size(36.dp)
                .then(if (rotateIcon) Modifier.rotate(180f) else Modifier)
        )
        
        Text(
            text = score.toString(),
            color = Color(0xFF00FFFF),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameOverButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(70.dp)
                .background(Color.Black)
                .border(
                    BorderStroke(3.dp, Color(0xFF00FFFF)),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF00FFFF),
                modifier = Modifier.size(42.dp)
            )
        }
        
        Text(
            text = label,
            color = Color(0xFF00FFFF),
            fontSize = 16.sp
        )
    }
}

// First, add this data class at the top of the file, before any composables
data class GameState(
    val winner: Int,
    val isBot: Boolean,
    val player1Queens: Int,
    val player2Queens: Int,
    val gridSize: Int
)

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    private var gameState by mutableStateOf(GameState(1, false, 0, 0, 6))
    
    // Add these properties
    private var player1QueenColor by mutableStateOf(queenColors[0]) // Default blue
    private var player2QueenColor by mutableStateOf(queenColors[1]) // Default pink

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize sound manager early
        soundManager = SoundManager(this)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            QueensRushTheme {
                var currentScene by remember { mutableStateOf(GameScene.MAIN_MENU) }
                var showSplashScreen by remember { mutableStateOf(true) }
                var selectedTime by remember { mutableStateOf(30) }
                
                if (showSplashScreen) {
                    SplashScreen {
                        showSplashScreen = false
                        soundManager.playBackgroundMusic()
                    }
                } else {
                    when (currentScene) {
                        GameScene.MAIN_MENU -> {
                            MainMenu(
                                onGameTypeSelected = { scene, time, color1, color2 ->
                                    player1QueenColor = color1
                                    player2QueenColor = color2
                                    selectedTime = time
                                    currentScene = scene
                                },
                                onSettingsClick = {
                                    currentScene = GameScene.SETTINGS
                                }
                            )
                        }
                        GameScene.SETTINGS -> {
                            SettingsPanel(
                                soundManager = soundManager,
                                onDismiss = {
                                    currentScene = GameScene.MAIN_MENU
                                },
                                onInfoClick = {
                                    currentScene = GameScene.INFO
                                }
                            )
                        }
                        GameScene.GAME_6X6, GameScene.GAME_8X8, 
                        GameScene.GAME_BOT_6X6, GameScene.GAME_BOT_8X8 -> {
                            GameBoard(
                                gridSize = if (currentScene == GameScene.GAME_6X6 || 
                                             currentScene == GameScene.GAME_BOT_6X6) 6 else 8,
                                selectedTime = selectedTime,
                                onBackPressed = {
                                    currentScene = GameScene.MAIN_MENU
                                },
                                soundManager = soundManager,
                                onGameOver = { state ->
                                    gameState = state
                                    currentScene = GameScene.GAME_OVER
                                },
                                onSettingsClick = {  // Add this parameter
                                    currentScene = GameScene.SETTINGS
                                },
                                player1Queen = player1QueenColor,
                                player2Queen = player2QueenColor,
                                isBot = currentScene == GameScene.GAME_BOT_6X6 || 
                                       currentScene == GameScene.GAME_BOT_8X8
                            )
                        }
                        GameScene.INFO -> {
                            InfoScreen(
                                soundManager = soundManager,
                                onDismiss = {
                                    currentScene = GameScene.SETTINGS
                                }
                            )
                        }
                        GameScene.GAME_OVER -> {
                            GameOverScreen(
                                winner = gameState.winner,
                                isBot = gameState.isBot,
                                player1Queens = gameState.player1Queens,
                                player2Queens = gameState.player2Queens,
                                onRestart = {
                                    currentScene = if (gameState.isBot) {
                                        if (gameState.gridSize == 6) GameScene.GAME_BOT_6X6 else GameScene.GAME_BOT_8X8
                                    } else {
                                        if (gameState.gridSize == 6) GameScene.GAME_6X6 else GameScene.GAME_8X8
                                    }
                                },
                                onMainMenu = {
                                    currentScene = GameScene.MAIN_MENU
                                },
                                soundManager = soundManager
                            )
                        }
                    }
                }
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        soundManager.pauseBackgroundMusic()
    }
    
    override fun onResume() {
        super.onResume()
        if (!soundManager.isMusicMuted) {
            soundManager.resumeBackgroundMusic()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun GameScreen() {
    var queenPositions by remember { 
        mutableStateOf(Array(8) { Array(8) { 0 } }) 
    }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(2.dp, Color(0xFF00FF00))
            ) {
                items(64) { index ->
                    val row = index / 8
                    val col = index % 8
                    
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .border(1.dp, Color.Gray)
                            .clickable {
                                if (canPlaceQueen(row, col, queenPositions)) {
                                    val newPositions = queenPositions.map { it.clone() }.toTypedArray()
                                    newPositions[row][col] = 1
                                    queenPositions = newPositions
                                } else {
                                    errorMessage.value = "Invalid move!"
                                }
                            }
                    ) {
                        if (queenPositions[row][col] != 0) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.star_big_on),
                                contentDescription = "Blue Queen",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFF00FF00)
                            )
                        }
                    }
                }
            }
        }

        errorMessage.value?.let { message ->
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    color = Color.Red,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                
                LaunchedEffect(message) {
                    delay(2000)
                    errorMessage.value = null
                }
            }
        }
    }
}

private fun canPlaceQueen(row: Int, col: Int, queenPositions: Array<Array<Int>>): Boolean {
    // Check if position is already occupied
    if (queenPositions[row][col] != 0) return false
    
    // Check for attacking queens
    for (r in queenPositions.indices) {
        for (c in queenPositions[r].indices) {
            if (queenPositions[r][c] != 0) {
                if (r == row || c == col || 
                    Math.abs(r - row) == Math.abs(c - col)) {
                    return false
                }
            }
        }
    }
    return true
}

// Replace the InfoScreen composable and InfoSection with this corrected version
@Composable
fun InfoScreen(
    soundManager: SoundManager,
    onDismiss: () -> Unit
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(backDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                soundManager.playButtonClick()
                onDismiss()
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with back button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        soundManager.playButtonClick()
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                        .border(
                            BorderStroke(2.dp, Color(0xFF00FFFF)),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF00FFFF),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Game Info",
                    color = Color(0xFF00FFFF),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                // Empty box for symmetry
                Box(modifier = Modifier.size(50.dp))
            }

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Game Story section
                InfoSectionContent(
                    title = "The Tale",
                    content = "In a realm of strategy and wit, two kingdoms face off in an epic battle of Queens. " +
                            "Each player commands powerful Queens, placing them strategically on the board. " +
                            "But beware! When Queens can capture each other, both kingdoms fall into chaos."
                )

                // Rules section
                InfoSectionContent(
                    title = "Rules",
                    content = "1. Players take turns placing Queens on the board\n\n" +
                            "2. Queens can move horizontally, vertically, and diagonally\n\n" +
                            "3. The game ends when a player places a Queen that can be captured by or can capture another Queen\n\n" +
                            "4. The player who placed the last Queen loses the game\n\n" +
                            "5. Each player has a limited number of Queens:\n" +
                            "   • 4 Queens each in 6x6 mode\n" +
                            "   • 5 Queens each in 8x8 mode"
                )

                // How to Play section
                InfoSectionContent(
                    title = "How to Play",
                    content = "1. Choose your game mode: PvP or PvBot\n\n" +
                            "2. Select board size: 6x6 or 8x8\n\n" +
                            "3. Set your preferred time limit\n\n" +
                            "4. Take turns placing Queens on the board\n\n" +
                            "5. Think carefully! Once placed, a Queen cannot be moved\n\n" +
                            "6. Use the hint feature in Bot mode if you need help"
                )

                // Tips section
                InfoSectionContent(
                    title = "Tips",
                    content = "• Plan your moves ahead\n\n" +
                            "• Watch out for diagonal attacks\n\n" +
                            "• Try to control the center of the board\n\n" +
                            "• Keep track of your opponent's remaining Queens\n\n" +
                            "• Use the time limit strategically"
                )
            }
        }
    }
}

@Composable
private fun InfoSectionContent(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF00FFFF),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Color(0xFF00FFFF)),
                    RoundedCornerShape(8.dp)
                )
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Text(
                text = content,
                color = Color(0xFF00FFFF),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}

// Add this composable for the mistake review screen
@Composable
fun MistakeReviewScreen(
    queenPositions: Array<Array<Int>>,
    lastMove: Pair<Int, Int>,
    gridSize: Int,
    isBot: Boolean,
    currentPlayer: Int,
    onSkip: () -> Unit,
    soundManager: SoundManager
) {
    var timeRemaining by remember { mutableStateOf(8) }
    
    // Countdown timer
    LaunchedEffect(Unit) {
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
            if (timeRemaining == 0) {
                onSkip()
            }
        }
    }

    // Transparent overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) { /* Prevent clicks through overlay */ },
        contentAlignment = Alignment.Center
    ) {
        // Message box
        Box(
            modifier = Modifier
                .padding(16.dp)
                .width(300.dp)
                .border(
                    BorderStroke(2.dp, Color(0xFF00FFFF)),
                    RoundedCornerShape(16.dp)
                )
                .background(Color.Black.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Game Over text with glow effect
                Box(contentAlignment = Alignment.Center) {
                    // Glow
                    Text(
                        text = "Game Over",
                        color = Color(0xFF00FFFF).copy(alpha = 0.4f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = 2.dp)
                    )
                    // Main text
                    Text(
                        text = "Game Over",
                        color = Color(0xFF00FFFF),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Message about the losing move
                Text(
                    text = if (isBot) {
                        if (currentPlayer == 1) "Your Queen was captured!" else "Clara's Queen was captured!"
                    } else {
                        "Player ${currentPlayer}'s Queen was captured!"
                    },
                    color = Color(0xFF00FFFF),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                // Skip button with timer
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color(0xFF00FFFF)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF00FFFF)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(160.dp)
                        .height(45.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 18.sp
                        )
                        Text(
                            text = " ($timeRemaining)",
                            fontSize = 16.sp,
                            color = Color(0xFF00FFFF).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }

    // Highlight the captured queen with pulsing effect
    val pulseAnimation by rememberInfiniteTransition().animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Add a red glow effect around the last move
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(pulseAnimation)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / gridSize
            val x = lastMove.second * cellSize + cellSize / 2
            val y = lastMove.first * cellSize + cellSize / 2
            
            drawCircle(
                color = Color.Red.copy(alpha = 0.3f),
                radius = cellSize * 0.8f,
                center = Offset(x, y),
                blendMode = BlendMode.Plus
            )
        }
    }
}

// Add this function at the top level, before any composable functions
private fun getTimerColor(currentTime: Int, totalTime: Int): Color {
    return when {
        currentTime <= (totalTime * 0.35) -> Color.Red  // Last 35% - Red
        else -> Color(0xFF00FF00)  // First 65% - Green
    }
}