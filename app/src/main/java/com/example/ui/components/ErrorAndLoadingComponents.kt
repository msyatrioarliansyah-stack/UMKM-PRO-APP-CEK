package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalIsDark

/**
 * Shimmer Brush for Loading Skeleton components
 */
@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1300f): Brush {
    return if (showShimmer) {
        val transition = rememberInfiniteTransition(label = "shimmer_transition")
        val translateAnimation by transition.animateFloat(
            initialValue = -300f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translation"
        )
        
        val isDark = LocalIsDark.current
        val shimmerColors = if (isDark) {
            listOf(
                Color(0xFF1E293B).copy(alpha = 0.6f),
                Color(0xFF334155).copy(alpha = 0.9f),
                Color(0xFF1E293B).copy(alpha = 0.6f),
            )
        } else {
            listOf(
                Color(0xFFE2E8F0).copy(alpha = 0.6f),
                Color(0xFFF1F5F9).copy(alpha = 0.9f),
                Color(0xFFE2E8F0).copy(alpha = 0.6f),
            )
        }
        
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnimation, translateAnimation),
            end = Offset(translateAnimation + 300f, translateAnimation + 300f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

/**
 * Standard Shimmer Box
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp)
) {
    Box(
        modifier = modifier
            .background(shimmerBrush(), shape = shape)
    )
}

/**
 * Skeleton Loader for raw material items or expense costs
 */
@Composable
fun BahanBakuItemSkeleton() {
    val isDark = LocalIsDark.current
    val cardBg = if (isDark) Color(0x0CFFFFFF) else Color(0x0A8B5CF6)
    val cardBorder = if (isDark) Color(0x1F00F0FF) else Color(0x158B5CF6)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBg, RoundedCornerShape(20.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Circle placeholder for Icon
                ShimmerBox(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Title placeholder
                    ShimmerBox(
                        modifier = Modifier.width(140.dp).height(18.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                    // Subtitle / category placeholder
                    ShimmerBox(
                        modifier = Modifier.width(80.dp).height(12.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Price placeholder
                ShimmerBox(
                    modifier = Modifier.width(90.dp).height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                // Stock placeholder
                ShimmerBox(
                    modifier = Modifier.width(50.dp).height(12.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
    }
}

/**
 * Skeleton Loader for Dashboard Summary Stat Cards
 */
@Composable
fun DashboardSummarySkeleton() {
    val isDark = LocalIsDark.current
    val cardBg = if (isDark) Color(0x15FFFFFF) else Color(0x99FFFFFF)
    val cardBorder = if (isDark) Color(0x4DFFFFFF) else Color(0x4D8B5CF6)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBg, RoundedCornerShape(28.dp))
            .border(1.5.dp, cardBorder, RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShimmerBox(modifier = Modifier.width(110.dp).height(14.dp), shape = RoundedCornerShape(4.dp))
                    ShimmerBox(modifier = Modifier.width(180.dp).height(28.dp), shape = RoundedCornerShape(6.dp))
                }
                ShimmerBox(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(16.dp))
            }
            
            HorizontalDivider(color = if (isDark) Color(0x1F00F0FF) else Color(0x1F8B5CF6))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerBox(modifier = Modifier.width(70.dp).height(12.dp), shape = RoundedCornerShape(4.dp))
                    ShimmerBox(modifier = Modifier.width(90.dp).height(16.dp), shape = RoundedCornerShape(4.dp))
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.End) {
                    ShimmerBox(modifier = Modifier.width(70.dp).height(12.dp), shape = RoundedCornerShape(4.dp))
                    ShimmerBox(modifier = Modifier.width(90.dp).height(16.dp), shape = RoundedCornerShape(4.dp))
                }
            }
        }
    }
}

val LocalErrorHandler = staticCompositionLocalOf<(Throwable) -> Unit> {
    { /* default no-op */ }
}

/**
 * Global Error Boundary composable that acts as a runtime safety net.
 * Direct exceptions in recompositions or manual callbacks can trigger this fallback UI.
 */
@Composable
fun ErrorBoundary(
    modifier: Modifier = Modifier,
    fallback: @Composable (Throwable, () -> Unit) -> Unit = { error, onReset ->
        DefaultErrorFallback(error = error, onReset = onReset)
    },
    content: @Composable () -> Unit
) {
    var errorState by remember { mutableStateOf<Throwable?>(null) }
    
    CompositionLocalProvider(LocalErrorHandler provides { errorState = it }) {
        if (errorState != null) {
            fallback(errorState!!, { errorState = null })
        } else {
            content()
        }
    }
}

/**
 * Premium Liquid Glassmorphic Error Fallback UI with diagnostics and active recovery
 */
@Composable
fun DefaultErrorFallback(
    error: Throwable,
    onReset: () -> Unit
) {
    val isDark = LocalIsDark.current
    val titleColor = if (isDark) Color.White else Color(0xFF1E293B)
    val textColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val btnColor = if (isDark) Color(0xFF00F0FF) else Color(0xFF8B5CF6)
    val btnTextColor = if (isDark) Color(0xFF020E26) else Color.White

    LiquidChromeBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                color = if (isDark) Color(0x1F00F0FF) else Color(0x1F8B5CF6),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0x4D00F0FF) else Color(0x4D8B5CF6),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Peringatan",
                            tint = Color(0xFFFF4C4C),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "Gangguan Sistem Terdeteksi",
                        color = titleColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Aplikasi mendeteksi ketidaksesuaian data sementara. Kami telah mengamankan data Anda. Silakan coba atur ulang modul.",
                        color = textColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp)
                            .background(
                                color = if (isDark) Color(0x1C000000) else Color(0x0C000000),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0x12FFFFFF) else Color(0x1A000000),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = error.stackTraceToString(),
                            color = if (isDark) Color(0xFFEF4444) else Color(0xFFB91C1C),
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = onReset,
                        colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = btnTextColor)
                            Text(
                                "Mulai Ulang Modul",
                                color = btnTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * iOS 27 glassmorphism style Empty State Component
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    illustrationRes: Int = com.example.R.drawable.img_empty_state,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val isDark = LocalIsDark.current
    val titleColor = if (isDark) Color.White else Color(0xFF1E293B)
    val textColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val actionBtnColor = if (isDark) Color(0xFF00F0FF) else Color(0xFF8B5CF6)
    val actionBtnTextColor = if (isDark) Color(0xFF020E26) else Color.White

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Container representing the glassmorphic futuristic aesthetic
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isDark) Color(0x0CFFFFFF) else Color(0x0A8B5CF6))
                    .border(
                        1.5.dp,
                        if (isDark) Color(0x1F00F0FF) else Color(0x1F8B5CF6),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = illustrationRes),
                    contentDescription = "Empty State Illustration",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Text(
                text = title,
                color = titleColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                color = textColor,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (actionText != null && onActionClick != null) {
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = actionBtnColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = actionText,
                        color = actionBtnTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

