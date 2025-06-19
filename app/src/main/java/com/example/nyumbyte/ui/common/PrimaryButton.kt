import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    // Get theme colors outside drawBehind
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    val infiniteTransition = rememberInfiniteTransition(label = "WaterFlow")

    // Create two independent animations for seamless looping
    val waveProgress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaterWave1"
    )

    val waveProgress2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaterWave2"
    )

    // Water-like colors with better blending
    val waterColors = listOf(
        primaryColor.copy(alpha = 0.85f),
        primaryContainer.copy(alpha = 0.8f),
        secondaryColor.copy(alpha = 0.75f)
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                shadowElevation = 8f
                shape = RoundedCornerShape(20.dp)
                clip = true
                alpha = if (enabled) 1f else 0.6f
            },
        shape = RoundedCornerShape(20.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = onPrimary,
            disabledContainerColor = Color.LightGray.copy(alpha = 0.7f),
            disabledContentColor = Color.Gray
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Base water color
                    drawRect(
                        color = primaryColor.copy(alpha = 0.9f),
                        size = size
                    )

                    // Wave effect using sine waves
                    for (i in 0..3) {
                        val amplitude = size.height / 8f
                        val frequency = 2f
                        val phase = 2 * PI * (waveProgress1 + i * 0.2f)
                        val verticalOffset = size.height * (0.7f - i * 0.1f)

                        val path = Path().apply {
                            moveTo(0f, verticalOffset)

                            // Draw wave pattern
                            for (x in 0..size.width.toInt() step 3) {
                                val y = verticalOffset + amplitude *
                                        sin(frequency * PI * x / size.width + phase).toFloat()
                                lineTo(x.toFloat(), y)
                            }

                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }

                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    waterColors[i % waterColors.size],
                                    waterColors[(i + 1) % waterColors.size]
                                )
                            ),
                            alpha = 0.5f - i * 0.1f
                        )
                    }

                    // Moving highlights for surface shimmer
                    for (i in 0..2) {
                        val highlightProgress = (waveProgress2 + i * 0.3f) % 1f
                        val x = size.width * highlightProgress
                        val y = size.height * (0.2f + i * 0.2f)

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f - i * 0.1f),
                                    Color.Transparent
                                ),
                                radius = size.width / 4f
                            ),
                            center = Offset(x, y),
                            radius = size.width / 6f
                        )
                    }
                }
                .clip(RoundedCornerShape(20.dp))
                ,
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.graphicsLayer {
                        alpha = 0.95f + 0.05f * kotlin.math.cos(waveProgress1 * 2 * PI).toFloat()
                    }
                )
                if (icon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.graphicsLayer {
                            translationY = 3f * kotlin.math.sin(waveProgress2 * 2 * PI).toFloat()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFECECEC))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                text = "Get Started",
                onClick = {},
                icon = Icons.Default.ArrowForward
            )
        }
    }
}