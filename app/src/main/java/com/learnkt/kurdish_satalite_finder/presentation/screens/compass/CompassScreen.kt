package com.learnkt.kurdish_satalite_finder.presentation.screens.compass

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings
import java.util.Locale
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompassScreen(
    onNavigateBack: () -> Unit,
    viewModel: CompassViewModel = hiltViewModel()
) {
    val satellite by viewModel.satellite.collectAsState()
    val currentAzimuth by viewModel.currentAzimuth.collectAsState()
    val calculation by viewModel.calculation.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val targetAzimuth = calculation?.azimuth?.toFloat() ?: 0f
    val diff = (targetAzimuth - currentAzimuth + 540) % 360 - 180
    val isAligned = abs(diff) < 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000827)) // Dark space blue
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = KurdishStrings.SELECT_SATELLITE,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            // Satellite Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Text(
                    text = satellite?.name ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Azimuth Value
            Text(
                text = "${KurdishStrings.AZIMUTH} ${String.format(Locale.US, "%.0f°", targetAzimuth)}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Guidance Text
            val guidanceText = if (isAligned) {
                KurdishStrings.PERFECT_ALIGNMENT
            } else {
                val dir = if (diff > 0) KurdishStrings.TO_RIGHT else KurdishStrings.TO_LEFT
                "${KurdishStrings.TURN_ANTENNA} ${String.format(Locale.US, "%.0f°", abs(diff))} $dir"
            }

            Text(
                text = guidanceText,
                color = if (isAligned) Color.Green else Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Compass View
            Box(contentAlignment = Alignment.Center) {
                CompassView(
                    currentAzimuth = currentAzimuth,
                    targetAzimuth = targetAzimuth,
                    modifier = Modifier.size(280.dp)
                )
                
                // Current Heading inside compass
                Text(
                    text = String.format(Locale.US, "%.0f°", currentAzimuth),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Location Panel
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = KurdishStrings.YOUR_LOCATION,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(KurdishStrings.LATITUDE, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = String.format(Locale.US, "%.3f", userLocation?.latitude ?: 0.0),
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(KurdishStrings.LONGITUDE, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = String.format(Locale.US, "%.3f", userLocation?.longitude ?: 0.0),
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompassView(
    currentAzimuth: Float,
    targetAzimuth: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Background Circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)
            ),
            radius = radius
        )

        // Outer Ring
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = radius,
            style = Stroke(width = 2.dp.toPx())
        )

        // Rotating Content (North, Target, etc)
        withTransform({
            rotate(-currentAzimuth, center)
        }) {
            // Target Needle
            val targetRad = Math.toRadians(targetAzimuth.toDouble() - 90.0)
            val targetX = center.x + (radius - 5.dp.toPx()) * cos(targetRad).toFloat()
            val targetY = center.y + (radius - 5.dp.toPx()) * sin(targetRad).toFloat()
            
            drawLine(
                color = Color.Red,
                start = center,
                end = Offset(targetX, targetY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Cardinal Marks
            drawCardinalMark("N", center, radius - 15.dp.toPx(), 0f)
            drawCardinalMark("E", center, radius - 15.dp.toPx(), 90f)
            drawCardinalMark("S", center, radius - 15.dp.toPx(), 180f)
            drawCardinalMark("W", center, radius - 15.dp.toPx(), 270f)
        }

        // Fixed Top Arrow (Pointer)
        val arrowPath = Path().apply {
            moveTo(center.x, center.y - radius - 10.dp.toPx())
            lineTo(center.x - 12.dp.toPx(), center.y - radius + 10.dp.toPx())
            lineTo(center.x + 12.dp.toPx(), center.y - radius + 10.dp.toPx())
            close()
        }
        drawPath(arrowPath, color = Color.White)
    }
}

fun DrawScope.drawCardinalMark(label: String, center: Offset, radius: Float, angle: Float) {
    val rad = Math.toRadians(angle.toDouble() - 90.0)
    val x = center.x + radius * cos(rad).toFloat()
    val y = center.y + radius * sin(rad).toFloat()
    drawCircle(Color.White, radius = 4.dp.toPx(), center = Offset(x, y))
}
