package com.learnkt.kurdish_satalite_finder.presentation.screens.detail

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatelliteDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCompass: (Int) -> Unit,
    viewModel: SatelliteDetailViewModel = hiltViewModel()
) {
    val satellite by viewModel.satellite.collectAsState()
    val calculations by viewModel.calculations.collectAsState()
    val locationError by viewModel.locationError.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

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
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (locationError) {
                Text(
                    text = "تکایە دەستگەیشتن بە شوێن (Location) چالاک بکە بۆ ئەوەی گۆشەکان بە دروستی حیساب بکرێن.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { viewModel.loadSatellite() }) {
                    Text("دووبارە هەوڵ بدەوە")
                }
            }

            // Current Location Display
            userLocation?.let { location ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = KurdishStrings.YOUR_LOCATION,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(KurdishStrings.LATITUDE, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = String.format(Locale.US, "%.4f", location.latitude),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(KurdishStrings.LONGITUDE, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = String.format(Locale.US, "%.4f", location.longitude),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }

            calculations?.let { calc ->
                CalculationCard(
                    title = KurdishStrings.AZIMUTH,
                    value = String.format(Locale.US, "%.2f°", calc.azimuth)
                )
                CalculationCard(
                    title = KurdishStrings.ELEVATION,
                    value = String.format(Locale.US, "%.2f°", calc.elevation)
                )
                CalculationCard(
                    title = KurdishStrings.LNB_SKEW,
                    value = String.format(Locale.US, "%.2f°", calc.lnbSkew)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { satellite?.let { onNavigateToCompass(it.id) } },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(KurdishStrings.HOME_COMPASS)
            }
        }
    }
}

@Composable
fun CalculationCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
