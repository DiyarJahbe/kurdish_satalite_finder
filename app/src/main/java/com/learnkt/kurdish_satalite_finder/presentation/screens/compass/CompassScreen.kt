package com.learnkt.kurdish_satalite_finder.presentation.screens.compass

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp
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
    val targetAzimuth by viewModel.targetAzimuth.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.loadSatellite()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val diff = (targetAzimuth - currentAzimuth + 540) % 360 - 180
    val isAligned = abs(diff) < 2

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(satellite?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isAligned) KurdishStrings.PERFECT_ALIGNMENT
                else if (diff > 0) KurdishStrings.TURN_RIGHT
                else KurdishStrings.TURN_LEFT,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isAligned) Color.Green else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            CompassView(
                currentAzimuth = currentAzimuth,
                targetAzimuth = targetAzimuth,
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = String.format(Locale.US, "${KurdishStrings.TARGET}: %.1f° | ${KurdishStrings.CURRENT}: %.1f°", targetAzimuth, currentAzimuth),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun CompassView(
    currentAzimuth: Float,
    targetAzimuth: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val targetColor = Color.Red

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Draw Compass Circle
        drawCircle(
            color = primaryColor,
            radius = radius,
            style = Stroke(width = 4.dp.toPx())
        )

        // Draw rotating content
        withTransform({
            rotate(-currentAzimuth, center)
        }) {
            // Draw cardinal points (Kurdish)
            drawTextCardinal(KurdishStrings.NORTH, center, radius - 25.dp.toPx(), 0f)
            drawTextCardinal(KurdishStrings.EAST, center, radius - 25.dp.toPx(), 90f)
            drawTextCardinal(KurdishStrings.SOUTH, center, radius - 25.dp.toPx(), 180f)
            drawTextCardinal(KurdishStrings.WEST, center, radius - 25.dp.toPx(), 270f)
            
            // Draw target line
            val targetRad = Math.toRadians(targetAzimuth.toDouble() - 90.0)
            val targetX = center.x + (radius - 10.dp.toPx()) * cos(targetRad).toFloat()
            val targetY = center.y + (radius - 10.dp.toPx()) * sin(targetRad).toFloat()
            
            drawLine(
                color = targetColor,
                start = center,
                end = Offset(targetX, targetY),
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw device center arrow (always up)
        val arrowPath = Path().apply {
            moveTo(center.x, center.y - radius + 20.dp.toPx())
            lineTo(center.x - 10.dp.toPx(), center.y - radius + 40.dp.toPx())
            lineTo(center.x + 10.dp.toPx(), center.y - radius + 40.dp.toPx())
            close()
        }
        drawPath(arrowPath, color = secondaryColor)
    }
}

fun DrawScope.drawTextCardinal(text: String, center: Offset, radius: Float, angle: Float) {
    val rad = Math.toRadians(angle.toDouble() - 90.0)
    val x = center.x + radius * cos(rad).toFloat()
    val y = center.y + radius * sin(rad).toFloat()
    
    drawCircle(Color.Gray, radius = 6.dp.toPx(), center = Offset(x, y))
}
