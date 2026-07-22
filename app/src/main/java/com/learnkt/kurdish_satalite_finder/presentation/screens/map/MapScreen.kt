package com.learnkt.kurdish_satalite_finder.presentation.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.material.icons.filled.*
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userLocation by viewModel.userLocation.collectAsState()
    val cameraPosition by viewModel.cameraPosition.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        cameraPosition ?: CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    val userMarkerState = rememberMarkerState(position = LatLng(0.0, 0.0))
    
    var mapType by remember { mutableIntStateOf(1) } // 1 = Normal by default
    
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true
            )
        )
    }

    val mapProperties by remember(mapType) {
        mutableStateOf(
            com.google.maps.android.compose.MapProperties(
                mapType = when (mapType) {
                    1 -> MapType.NORMAL
                    2 -> MapType.HYBRID
                    else -> MapType.NORMAL
                }
            )
        )
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            userMarkerState.position = latLng
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    }

    LaunchedEffect(cameraPosition) {
        cameraPosition?.let {
            cameraPositionState.position = it
        }
    }

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = mapUiSettings,
                properties = mapProperties
            ) {
                if (userLocation != null) {
                    Marker(
                        state = userMarkerState,
                        title = "شوێنی تۆ",
                        snippet = "پانی: ${"%.4f".format(userLocation!!.latitude)}، درێژی: ${"%.4f".format(userLocation!!.longitude)}"
                    )
                }
            }

            // Floating Search Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { 
                        viewModel.searchLocation(it)
                        isSearchActive = false
                    },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text(KurdishStrings.SEARCH_LOCATION) },
                    leadingIcon = {
                        IconButton(onClick = { 
                            if (isSearchActive) isSearchActive = false else onNavigateBack()
                        }) {
                            Icon(
                                if (isSearchActive) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                            IconButton(onClick = { /* Voice search */ }) {
                                Icon(Icons.Default.Mic, contentDescription = null)
                            }
                            IconButton(onClick = { /* Profile */ }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp
                ) {
                    // Search suggestions
                }
                
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 4.dp, end = 8.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Map Layer Toggle (Floating) - Moved lower to avoid overlap
            FloatingActionButton(
                onClick = { mapType = if (mapType == 1) 2 else 1 },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 110.dp, end = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(Icons.Default.Layers, contentDescription = "Map Layers")
            }

            // My Location FAB - Standard Google Maps position
            FloatingActionButton(
                onClick = { viewModel.refreshLocation() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 110.dp, end = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    imageVector = if (userLocation != null) Icons.Default.MyLocation else Icons.Default.LocationSearching,
                    contentDescription = "My Location"
                )
            }

            // Zoom controls (Floating on the right side center)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZoomButton(
                    icon = Icons.Default.Add,
                    onClick = {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            cameraPositionState.position.target,
                            cameraPositionState.position.zoom + 1f
                        )
                    }
                )
                ZoomButton(
                    icon = Icons.Default.Remove,
                    onClick = {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            cameraPositionState.position.target,
                            cameraPositionState.position.zoom - 1f
                        )
                    }
                )
            }

            // Location info card (Bottom Center-ish)
            if (userLocation != null && !isSearchActive) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "شوێنی هەنووکەیی تۆ",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (userLocation!!.hasAccuracy()) {
                                Text(
                                    text = "±${"%.0f".format(userLocation!!.accuracy)} م",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                LocationInfo("پانی", "%.4f°".format(userLocation!!.latitude))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                LocationInfo("درێژی", "%.4f°".format(userLocation!!.longitude))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LocationInfo(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

