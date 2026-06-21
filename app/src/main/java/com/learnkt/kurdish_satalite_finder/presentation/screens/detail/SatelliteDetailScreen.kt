package com.learnkt.kurdish_satalite_finder.presentation.screens.detail

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
