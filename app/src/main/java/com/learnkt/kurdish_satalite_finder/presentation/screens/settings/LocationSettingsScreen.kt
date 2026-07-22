package com.learnkt.kurdish_satalite_finder.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: LocationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(KurdishStrings.LOCATION_SETTINGS) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // GPS Auto-detect Toggle
            item {
                LocationModeCard(
                    title = KurdishStrings.USE_GPS,
                    icon = Icons.Default.GpsFixed,
                    isSelected = uiState.useAutoGps,
                    onClick = { viewModel.setUseAutoGps(true) }
                )
            }

            item {
                LocationModeCard(
                    title = KurdishStrings.MANUAL_COORDINATES,
                    icon = Icons.Default.EditLocation,
                    isSelected = !uiState.useAutoGps,
                    onClick = { viewModel.setUseAutoGps(false) }
                )
            }

            if (!uiState.useAutoGps) {
                item {
                    ManualCoordinatesInput(
                        latitude = uiState.manualLatitude,
                        longitude = uiState.manualLongitude,
                        onLatChange = { viewModel.updateManualLatitude(it) },
                        onLonChange = { viewModel.updateManualLongitude(it) }
                    )
                }

                item {
                    Text(
                        text = KurdishStrings.SELECT_CITY,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(KurdishCities.list) { city ->
                    CityItem(
                        city = city,
                        isSelected = uiState.selectedCity == city.name,
                        onClick = { viewModel.selectCity(city) }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { 
                        viewModel.saveSettings()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(KurdishStrings.SAVE_LOCATION)
                }
            }
        }
    }
}

@Composable
fun LocationModeCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(selected = isSelected, onClick = onClick)
        }
    }
}

@Composable
fun ManualCoordinatesInput(
    latitude: String,
    longitude: String,
    onLatChange: (String) -> Unit,
    onLonChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = latitude,
                onValueChange = onLatChange,
                label = { Text(KurdishStrings.LATITUDE) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = longitude,
                onValueChange = onLonChange,
                label = { Text(KurdishStrings.LONGITUDE) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }
}

@Composable
fun CityItem(
    city: KurdishCities.City,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = city.name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

object KurdishCities {
    data class City(val name: String, val lat: Double, val lon: Double)
    val list = listOf(
        City("ھەولێر (Erbil)", 36.1911, 44.0092),
        City("سلێمانی (Sulaymaniyah)", 35.5617, 45.4333),
        City("دھۆک (Duhok)", 36.8601, 42.9904),
        City("کەرکووک (Kirkuk)", 35.4687, 44.3922),
        City("ھەڵەبجە (Halabja)", 35.1777, 45.9861),
        City("زاخۆ (Zakho)", 37.1436, 42.6775),
        City("گەرمیان (Garmian)", 34.6214, 44.9358),
        City("سۆران (Soran)", 36.6547, 44.5458),
        City("ڕانیە (Ranya)", 36.2551, 44.8824)
    )
}
