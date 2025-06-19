package com.example.nyumbyte.ui.beta

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Liquid Glass with Dynamic Light Reflections
 * • Animated rainbow border effect
 * • Moving light reflections instead of waves
 * • Circular light reflection with motion
 * • Pure external shadow
 */
data class LiquidGlassConfig(
    val cornerRadius: Dp = 24.dp,
    val glassAlpha: Float = 0.22f,
    val shadowElevation: Dp = 8.dp,
    val rainbowSize: Dp = 4.dp,
    val lightSpeed: Float = 1f // Animation speed multiplier
)

@Composable
fun LiquidGlass(
    modifier: Modifier = Modifier,
    config: LiquidGlassConfig = LiquidGlassConfig(),
    content: @Composable BoxScope.() -> Unit
) {
    // Animation controllers
    val infiniteTransition = rememberInfiniteTransition()

    // Primary light position (circular reflection)
    val lightPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween((8000 / config.lightSpeed).toInt(), easing = LinearEasing)
        )
    )

    // Rainbow animation
    val rainbowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween((10000 / config.lightSpeed).toInt(), easing = LinearEasing)
        )
    )

    // Glass colors
    val glassColor = Color(0x88FFFFFF)  // Frosted white
    val highlightColor = Color.White.copy(alpha = 0.2f)
    val edgeColor = Color.White.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            // External shadow only
            .shadow(
                elevation = config.shadowElevation,
                shape = RoundedCornerShape(config.cornerRadius),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            // Animated rainbow border
            .drawBehind {
                drawRainbowBorder(
                    size = size,
                    cornerRadius = config.cornerRadius.toPx(),
                    borderSize = config.rainbowSize.toPx(),
                    phase = rainbowPhase
                )
            }
            .clip(RoundedCornerShape(config.cornerRadius))
            .graphicsLayer {
                shape = RoundedCornerShape(config.cornerRadius)
                clip = true
            }
            .drawWithContent {
                // 1. Frosted glass base
                drawRoundRect(
                    color = glassColor.copy(alpha = config.glassAlpha),
                    cornerRadius = CornerRadius(config.cornerRadius.toPx())
                )

                // 2. Moving light reflections
                drawMovingLights(size, lightPosition)

                // 3. Content
                drawContent()

                // 4. Edge highlight
                drawRoundRect(
                    color = edgeColor,
                    cornerRadius = CornerRadius(config.cornerRadius.toPx()),
                    style = Stroke(width = 1.5f)
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            content = content
        )
    }
}

private fun DrawScope.drawRainbowBorder(
    size: Size,
    cornerRadius: Float,
    borderSize: Float,
    phase: Float
) {
    // Rainbow colors with phase-based offset
    val rainbowColors = listOf(
        Color(0x99FF6B6B), // Red
        Color(0xAAFFD166), // Orange
        Color(0xBBFFE66D), // Yellow
        Color(0xAA4ECDC4), // Cyan
        Color(0x996B5B95)  // Purple
    )

    // Create border area
    val borderRect = Rect(
        left = -borderSize,
        top = -borderSize,
        right = size.width + borderSize,
        bottom = size.height + borderSize
    )

    // Animate the gradient center
    val offsetX = size.width * phase
    val offsetY = size.height * (0.3f + 0.4f * sin(phase * PI.toFloat() * 2))

    // Draw animated rainbow border
    drawRoundRect(
        brush = Brush.sweepGradient(
            rainbowColors,
            center = Offset(offsetX, offsetY)
        ),
        topLeft = borderRect.topLeft,
        size = borderRect.size,
        cornerRadius = CornerRadius(cornerRadius + borderSize),
        style = Stroke(width = borderSize * 1.5f)
    )
}

private fun DrawScope.drawMovingLights(
    size: Size,
    position: Float
) {
    // Calculate positions based on animation phase
    val primaryX = size.width * 0.7f
    val primaryY = size.height * (0.2f + 0.6f * position)

    // Secondary light position (opposite direction)
    val secondaryX = size.width * 0.3f
    val secondaryY = size.height * (0.8f - 0.6f * position)

    // 1. Primary circular light reflection
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = Offset(primaryX, primaryY),
            radius = size.minDimension * 0.4f
        ),
        center = Offset(primaryX, primaryY),
        radius = size.minDimension * 0.4f,
        blendMode = BlendMode.Screen
    )

    // 2. Secondary light streak
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.2f),
                Color.Transparent
            ),
            start = Offset(secondaryX - size.width * 0.2f, secondaryY),
            end = Offset(secondaryX + size.width * 0.2f, secondaryY)
        ),
        topLeft = Offset(0f, secondaryY - 2f),
        size = Size(size.width, 4f),
        blendMode = BlendMode.Screen
    )

    // 3. Floating light particles
    for (i in 0..5) {
        val offset = (position * 2 + i * 0.2f) % 1f
        val particleX = size.width * (0.1f + 0.8f * offset)
        val particleY = size.height * (0.1f + 0.8f * (1 - offset * offset))

        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            center = Offset(particleX, particleY),
            radius = 4f,
            blendMode = BlendMode.Screen
        )
    }
}

/**
 * Liquid Glass Text
 */
@Composable
fun LiquidGlassText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = MaterialTheme.typography.titleMedium.copy(
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.5f),
                offset = Offset(1f, 1f),
                blurRadius = 4f
            )
        )
    )
}

/**
 * Liquid Glass Demo with Dynamic Lights
 */
@Composable
fun LiquidGlassDemo() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1D2B3D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Primary panel
            LiquidGlass(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                config = LiquidGlassConfig(
                    glassAlpha = 0.25f,
                    shadowElevation = 12.dp,
                    rainbowSize = 6.dp,
                    lightSpeed = 1f
                )
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LiquidGlassText("Dynamic Light Reflections")
                }
            }

            // Dual panels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LiquidGlass(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    config = LiquidGlassConfig(
                        cornerRadius = 16.dp,
                        shadowElevation = 8.dp,
                        rainbowSize = 4.dp,
                        lightSpeed = 0.8f
                    )
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LiquidGlassText("Moving Lights")
                    }
                }

                LiquidGlass(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    config = LiquidGlassConfig(
                        cornerRadius = 16.dp,
                        glassAlpha = 0.18f,
                        shadowElevation = 8.dp,
                        rainbowSize = 4.dp,
                        lightSpeed = 1.2f
                    )
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LiquidGlassText("Animated Rainbow")
                    }
                }
            }

            // Content panel
            LiquidGlass(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                config = LiquidGlassConfig(
                    cornerRadius = 28.dp,
                    glassAlpha = 0.28f,
                    shadowElevation = 16.dp,
                    rainbowSize = 8.dp,
                    lightSpeed = 0.7f
                )
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LiquidGlassText("Premium Experience", color = Color.White)
                    Spacer(Modifier.height(16.dp))
                    LiquidGlassText("Smooth Light Motion", color = Color(0xFFE0F7FA))
                }
            }
        }
    }
}