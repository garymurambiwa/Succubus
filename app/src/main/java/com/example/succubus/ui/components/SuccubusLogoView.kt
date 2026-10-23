package com.example.succubus.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.succubus.R
import com.example.succubus.model.LogoStyle
import com.example.succubus.model.VenueSettings
import com.example.succubus.ui.theme.BarBlack
import com.example.succubus.ui.theme.SpeakeasyGold
import com.example.succubus.ui.theme.SpeakeasyGoldLight
import com.example.succubus.ui.theme.SuccubusRed
import com.example.succubus.ui.theme.TextIvory
import com.example.succubus.ui.theme.TextMuted
import java.io.File

@Composable
fun DevilHeartCanvas(
    modifier: Modifier = Modifier,
    color: Color = SuccubusRed,
    strokeWidth: Float = 12f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Left horn
        val leftHorn = Path().apply {
            moveTo(w * 0.38f, h * 0.22f)
            lineTo(w * 0.33f, h * 0.08f)
            lineTo(w * 0.44f, h * 0.18f)
            close()
        }
        drawPath(leftHorn, color)

        // Right horn
        val rightHorn = Path().apply {
            moveTo(w * 0.62f, h * 0.22f)
            lineTo(w * 0.67f, h * 0.08f)
            lineTo(w * 0.56f, h * 0.18f)
            close()
        }
        drawPath(rightHorn, color)

        // Devil Heart Body
        val heartPath = Path().apply {
            moveTo(w * 0.5f, h * 0.28f)
            // Left lobe
            cubicTo(
                w * 0.45f, h * 0.12f,
                w * 0.22f, h * 0.15f,
                w * 0.20f, h * 0.36f
            )
            cubicTo(
                w * 0.18f, h * 0.52f,
                w * 0.34f, h * 0.68f,
                w * 0.5f, h * 0.82f
            )
            // Right lobe
            moveTo(w * 0.5f, h * 0.28f)
            cubicTo(
                w * 0.55f, h * 0.12f,
                w * 0.78f, h * 0.15f,
                w * 0.80f, h * 0.36f
            )
            cubicTo(
                w * 0.82f, h * 0.52f,
                w * 0.66f, h * 0.68f,
                w * 0.5f, h * 0.82f
            )
        }
        drawPath(
            heartPath,
            color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Devil's tail with arrowhead
        val tailPath = Path().apply {
            moveTo(w * 0.5f, h * 0.82f)
            cubicTo(
                w * 0.46f, h * 0.88f,
                w * 0.52f, h * 0.96f,
                w * 0.68f, h * 0.94f
            )
            cubicTo(
                w * 0.78f, h * 0.92f,
                w * 0.82f, h * 0.82f,
                w * 0.80f, h * 0.72f
            )
        }
        drawPath(
            tailPath,
            color,
            style = Stroke(
                width = strokeWidth * 0.9f,
                cap = StrokeCap.Round
            )
        )

        // Devil tail arrow point
        val tailArrow = Path().apply {
            moveTo(w * 0.80f, h * 0.72f)
            lineTo(w * 0.87f, h * 0.74f)
            lineTo(w * 0.80f, h * 0.64f)
            close()
        }
        drawPath(tailArrow, color)
    }
}

@Composable
fun SuccubusBrandBanner(
    settings: VenueSettings,
    modifier: Modifier = Modifier,
    height: Dp = 150.dp,
    showTagline: Boolean = true
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(BarBlack)
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        SpeakeasyGold.copy(alpha = 0.5f),
                        SpeakeasyGoldLight,
                        SpeakeasyGold.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Inner double border effect matching the original luxury logo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 0.8.dp,
                    color = SpeakeasyGold.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            when (settings.logoStyle) {
                LogoStyle.ORIGINAL_BANNER -> {
                    // Try to load bitmap from assets or drawable, with elegant fallback
                    val assetBitmap = remember(settings.logoStyle) {
                        try {
                            context.assets.open("succubus_logo.png").use { stream ->
                                BitmapFactory.decodeStream(stream)?.asImageBitmap()
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (assetBitmap != null) {
                        Image(
                            bitmap = assetBitmap,
                            contentDescription = settings.venueName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // High-fidelity programmatic vector logo matching the user's uploaded artboard
                        DynamicVectorBrandedBanner(settings = settings, showTagline = showTagline)
                    }
                }
                LogoStyle.DEVIL_HEART_EMBLEM -> {
                    DynamicVectorBrandedBanner(settings = settings, showTagline = showTagline)
                }
                LogoStyle.MODERN_MINIMAL -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DevilHeartCanvas(
                            modifier = Modifier.size(54.dp),
                            color = SuccubusRed,
                            strokeWidth = 6f
                        )
                        Text(
                            text = settings.venueName,
                            color = SpeakeasyGoldLight,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 6.sp,
                            fontFamily = FontFamily.Serif
                        )
                        if (showTagline && settings.tagline.isNotBlank()) {
                            Text(
                                text = settings.tagline,
                                color = TextMuted,
                                fontSize = 10.sp,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
                LogoStyle.CUSTOM_URL -> {
                    val customBitmap = remember(settings.customLogoUri) {
                        try {
                            if (settings.customLogoUri.isNotBlank()) {
                                val file = File(settings.customLogoUri)
                                if (file.exists()) {
                                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                                } else null
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (customBitmap != null) {
                        Image(
                            bitmap = customBitmap,
                            contentDescription = settings.venueName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        DynamicVectorBrandedBanner(settings = settings, showTagline = showTagline)
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicVectorBrandedBanner(
    settings: VenueSettings,
    showTagline: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        DevilHeartCanvas(
            modifier = Modifier.size(62.dp),
            color = SuccubusRed,
            strokeWidth = 7.5f
        )
        Text(
            text = settings.venueName,
            color = SpeakeasyGoldLight,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 8.sp,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(top = 4.dp)
        )
        if (showTagline && settings.tagline.isNotBlank()) {
            Text(
                text = settings.tagline,
                color = TextIvory.copy(alpha = 0.85f),
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Composable
fun BrandCompactHeader(
    settings: VenueSettings,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BarBlack)
            .border(
                width = 1.dp,
                color = SpeakeasyGold.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DevilHeartCanvas(
                    modifier = Modifier.size(34.dp),
                    color = SuccubusRed,
                    strokeWidth = 4.5f
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = settings.venueName,
                        color = SpeakeasyGoldLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = settings.tagline.take(45) + if (settings.tagline.length > 45) "..." else "",
                        color = TextMuted,
                        fontSize = 9.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Station badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF231C28))
                    .border(0.8.dp, SpeakeasyGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = settings.stationName,
                    color = SpeakeasyGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
