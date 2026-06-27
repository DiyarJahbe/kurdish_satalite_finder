package com.learnkt.kurdish_satalite_finder.presentation.screens.compass

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
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
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.loadSatellite()
            viewModel.startLocationUpdates()
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

    val targetAzimuth = calculation?.azimuth?.toFloat() ?: 0f
    val diff = (targetAzimuth - currentAzimuth + 540) % 360 - 180
    val isAligned = abs(diff) < 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000827))
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

            // Map and Compass Circle
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Live Map in the circle
                userLocation?.let { location ->
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(userLatLng, 18f)
                    }
                    
                    // Update camera position when location changes
                    LaunchedEffect(location) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 18f)
                    }

                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .clip(CircleShape),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false,
                            compassEnabled = false,
                            mapToolbarEnabled = false
                        ),
                        properties = MapProperties(
                            mapType = MapType.SATELLITE,
                            isMyLocationEnabled = true
                        )
                    )
                }

                // Overlay Compass
                CompassOverlay(
                    currentAzimuth = currentAzimuth,
                    targetAzimuth = targetAzimuth,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Current Heading
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.0f°", currentAzimuth),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
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
fun CompassOverlay(
    currentAzimuth: Float,
    targetAzimuth: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Outer Ring
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = radius,
            style = Stroke(width = 3.dp.toPx())
        )

        // Degree Marks and Cardinal Points
        withTransform({
            rotate(-currentAzimuth, center)
        }) {
            // Target Needle (Red)
            val targetRad = Math.toRadians(targetAzimuth.toDouble() - 90.0)
            val targetX = center.x + (radius - 5.dp.toPx()) * cos(targetRad).toFloat()
            val targetY = center.y + (radius - 5.dp.toPx()) * sin(targetRad).toFloat()
            
            drawLine(
                color = Color.Red,
                start = center,
                end = Offset(targetX, targetY),
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Cardinal Marks
            drawCardinalMark("N", center, radius - 20.dp.toPx(), 0f)
            drawCardinalMark("E", center, radius - 20.dp.toPx(), 90f)
            drawCardinalMark("S", center, radius - 20.dp.toPx(), 180f)
            drawCardinalMark("W", center, radius - 20.dp.toPx(), 270f)
            
            // Tick marks every 10 degrees
            for (angle in 0 until 360 step 10) {
                val angleRad = Math.toRadians(angle.toDouble() - 90.0)
                val startX = center.x + (radius - 15.dp.toPx()) * cos(angleRad).toFloat()
                val startY = center.y + (radius - 15.dp.toPx()) * sin(angleRad).toFloat()
                val endX = center.x + radius * cos(angleRad).toFloat()
                val endY = center.y + radius * sin(angleRad).toFloat()
                
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Fixed Top Pointer
        val arrowPath = Path().apply {
            moveTo(center.x, center.y - radius - 15.dp.toPx())
            lineTo(center.x - 15.dp.toPx(), center.y - radius + 10.dp.toPx())
            lineTo(center.x + 15.dp.toPx(), center.y - radius + 10.dp.toPx())
            close()
        }
        drawPath(arrowPath, color = Color.White)
    }
}

fun DrawScope.drawCardinalMark(label: String, center: Offset, radius: Float, angle: Float) {
    val rad = Math.toRadians(angle.toDouble() - 90.0)
    val x = center.x + radius * cos(rad).toFloat()
    val y = center.y + radius * sin(rad).toFloat()
    drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(x, y))
}
