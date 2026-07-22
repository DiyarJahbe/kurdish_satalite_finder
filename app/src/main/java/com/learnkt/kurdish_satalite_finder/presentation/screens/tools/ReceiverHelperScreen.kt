package com.learnkt.kurdish_satalite_finder.presentation.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiverHelperScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(KurdishStrings.RECEIVER_HELPER) },
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
            item {
                GuideCard(
                    title = KurdishStrings.LNB_GUIDE_TITLE,
                    description = KurdishStrings.LNB_GUIDE_DESC,
                    icon = Icons.Default.SettingsInputAntenna
                )
            }
            
            item {
                GuideCard(
                    title = KurdishStrings.SCAN_SETTINGS_TITLE,
                    description = KurdishStrings.SCAN_SETTINGS_DESC,
                    icon = Icons.Default.Search
                )
            }
            
            item {
                GuideCard(
                    title = "Frequency Examples",
                    description = "Nilesat: 11747 V 27500\nTürksat: 12054 H 27500\nHotbird: 11034 V 27500",
                    icon = Icons.Default.Numbers
                )
            }
        }
    }
}

@Composable
fun GuideCard(title: String, description: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
