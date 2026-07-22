package com.learnkt.kurdish_satalite_finder.presentation.screens.ar

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings
import java.util.Locale
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARScreen(
    satelliteId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ARViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val satellite by viewModel.satellite.collectAsState()
    val calculation by viewModel.calculation.collectAsState()
    val currentAzimuth by viewModel.currentAzimuth.collectAsState()
    val currentPitch by viewModel.currentPitch.collectAsState()
    val currentRoll by viewModel.currentRoll.collectAsState()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.loadSatellite(satelliteId)
        }
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        cameraPermissionLauncher.launch(permissions.toTypedArray())
        viewModel.loadSatellite(satelliteId)
        viewModel.startSensors()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSensors()
        }
    }

    val targetAzimuth = calculation?.azimuth?.toFloat() ?: 0f
    val targetElevation = calculation?.elevation?.toFloat() ?: 0f

    val azimuthDiff = normalizeAngle(targetAzimuth - currentAzimuth)
    val elevationDiff = targetElevation - currentPitch

    val isAligned = abs(azimuthDiff) < 5f && abs(elevationDiff) < 5f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (exc: Exception) {
                        // Handle camera binding errors
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // AR Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Draw crosshair in center
            drawCrosshair(centerX, centerY)

            // Draw target indicator based on azimuth and elevation
            val indicatorX = centerX + (azimuthDiff * size.width / 90f)
            val indicatorY = centerY - (elevationDiff * size.height / 90f)

            // Draw target circle
            drawTargetIndicator(
                x = indicatorX.coerceIn(0f, size.width),
                y = indicatorY.coerceIn(0f, size.height),
                isAligned = isAligned
            )

            // Draw azimuth arc
            drawAzimuthArc(centerX, centerY, size.minDimension / 3, currentAzimuth, targetAzimuth)
        }

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = satellite?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Guidance Panel
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isAligned) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Green.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = KurdishStrings.PERFECT_ALIGNMENT,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom Info Panel
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = KurdishStrings.AZIMUTH,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f°", targetAzimuth)} (Target)",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f°", currentAzimuth)} (Current)",
                    color = if (abs(azimuthDiff) < 5f) Color.Green else Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = KurdishStrings.ELEVATION,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f°", targetElevation)} (Target)",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f°", currentPitch)} (Current)",
                    color = if (abs(elevationDiff) < 5f) Color.Green else Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Direction guidance
                val azimuthGuidance = when {
                    azimuthDiff > 5f -> "Turn Right ${String.format(Locale.US, "%.0f°", abs(azimuthDiff))}"
                    azimuthDiff < -5f -> "Turn Left ${String.format(Locale.US, "%.0f°", abs(azimuthDiff))}"
                    else -> "Azimuth Aligned"
                }

                val elevationGuidance = when {
                    elevationDiff > 5f -> "Tilt Up ${String.format(Locale.US, "%.0f°", abs(elevationDiff))}"
                    elevationDiff < -5f -> "Tilt Down ${String.format(Locale.US, "%.0f°", abs(elevationDiff))}"
                    else -> "Elevation Aligned"
                }

                Text(
                    text = azimuthGuidance,
                    color = if (abs(azimuthDiff) < 5f) Color.Green else Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = elevationGuidance,
                    color = if (abs(elevationDiff) < 5f) Color.Green else Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun normalizeAngle(angle: Float): Float {
    var result = angle % 360
    if (result > 180) result -= 360
    if (result < -180) result += 360
    return result
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCrosshair(
    centerX: Float,
    centerY: Float
) {
    val crosshairSize = 100f
    val strokeWidth = 2f

    // Horizontal line
    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(centerX - crosshairSize, centerY),
        end = Offset(centerX + crosshairSize, centerY),
        strokeWidth = strokeWidth
    )

    // Vertical line
    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(centerX, centerY - crosshairSize),
        end = Offset(centerX, centerY + crosshairSize),
        strokeWidth = strokeWidth
    )

    // Center circle
    drawCircle(
        color = Color.White.copy(alpha = 0.5f),
        radius = 20f,
        center = Offset(centerX, centerY),
        style = Stroke(width = strokeWidth)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTargetIndicator(
    x: Float,
    y: Float,
    isAligned: Boolean
) {
    val targetColor = if (isAligned) Color.Green else Color.Red
    val targetSize = 60f

    // Outer circle
    drawCircle(
        color = targetColor.copy(alpha = 0.3f),
        radius = targetSize,
        center = Offset(x, y)
    )

    // Inner circle
    drawCircle(
        color = targetColor.copy(alpha = 0.6f),
        radius = targetSize * 0.6f,
        center = Offset(x, y)
    )

    // Center dot
    drawCircle(
        color = targetColor,
        radius = 8f,
        center = Offset(x, y)
    )

    // Arrow pointing to center
    val arrowSize = 20f
    val centerX = size.width / 2
    val centerY = size.height / 2
    val angle = atan2(centerY - y, centerX - x)

    val arrowPath = androidx.compose.ui.graphics.Path().apply {
        val arrowX = x + cos(angle) * (targetSize + 10f)
        val arrowY = y + sin(angle) * (targetSize + 10f)
        moveTo(arrowX, arrowY)
        lineTo(
            arrowX - cos(angle - PI / 6).toFloat() * arrowSize,
            arrowY - sin(angle - PI / 6).toFloat() * arrowSize
        )
        lineTo(
            arrowX - cos(angle + PI / 6).toFloat() * arrowSize,
            arrowY - sin(angle + PI / 6).toFloat() * arrowSize
        )
        close()
    }
    drawPath(arrowPath, color = targetColor)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAzimuthArc(
    centerX: Float,
    centerY: Float,
    radius: Float,
    currentAzimuth: Float,
    targetAzimuth: Float
) {
    // Draw compass arc at bottom
    val arcY = centerY + radius * 1.5f
    val arcRadius = radius * 0.8f

    // Background arc
    drawArc(
        color = Color.White.copy(alpha = 0.2f),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(centerX - arcRadius, arcY - arcRadius),
        size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
        style = Stroke(width = 8f)
    )

    // Current azimuth indicator
    val currentAngle = 180f + currentAzimuth
    drawArc(
        color = Color.Blue,
        startAngle = currentAngle - 10f,
        sweepAngle = 20f,
        useCenter = false,
        topLeft = Offset(centerX - arcRadius, arcY - arcRadius),
        size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
        style = Stroke(width = 8f)
    )

    // Target azimuth indicator
    val targetAngle = 180f + targetAzimuth
    drawArc(
        color = Color.Red,
        startAngle = targetAngle - 10f,
        sweepAngle = 20f,
        useCenter = false,
        topLeft = Offset(centerX - arcRadius, arcY - arcRadius),
        size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
        style = Stroke(width = 8f)
    )
}
