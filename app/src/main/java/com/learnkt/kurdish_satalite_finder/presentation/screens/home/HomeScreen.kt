package com.learnkt.kurdish_satalite_finder.presentation.screens.home

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Satellite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.presentation.navigation.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf("home") }
    var searchQuery by remember { mutableStateOf("") }
    
    val satellites by viewModel.satellites.collectAsState()
    val favoriteSatellites by viewModel.favoriteSatellites.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        when (selectedTab) {
                            "home" -> KurdishStrings.APP_NAME
                            "satellites" -> KurdishStrings.HOME_SATELLITES
                            "favorites" -> KurdishStrings.HOME_FAVORITES
                            else -> KurdishStrings.APP_NAME
                        }
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home" },
                    icon = {
                        Icon(
                            if (selectedTab == "home") Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = null
                        )
                    },
                    label = { Text(KurdishStrings.HOME_TITLE) }
                )
                NavigationBarItem(
                    selected = selectedTab == "satellites",
                    onClick = { selectedTab = "satellites" },
                    icon = {
                        Icon(
                            if (selectedTab == "satellites") Icons.Filled.Satellite else Icons.Outlined.Satellite,
                            contentDescription = null
                        )
                    },
                    label = { Text(KurdishStrings.HOME_SATELLITES) }
                )
                NavigationBarItem(
                    selected = selectedTab == "favorites",
                    onClick = { selectedTab = "favorites" },
                    icon = {
                        Icon(
                            if (selectedTab == "favorites") Icons.Filled.Favorite else Icons.Outlined.Favorite,
                            contentDescription = null
                        )
                    },
                    label = { Text(KurdishStrings.HOME_FAVORITES) }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            "home" -> HomeTabContent(
                modifier = Modifier.padding(padding),
                userLocation = userLocation,
                onRefreshLocation = { viewModel.refreshLocation() },
                totalSatellites = satellites.size,
                favoriteCount = favoriteSatellites.size,
                onNavigateToSatellites = { selectedTab = "satellites" },
                onNavigateToFavorites = { selectedTab = "favorites" },
                onNavigateToMap = { navController.navigate(Screen.Map.route) }
            )
            "satellites" -> SatellitesTabContent(
                modifier = Modifier.padding(padding),
                navController = navController,
                satellites = satellites,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
            "favorites" -> FavoritesTabContent(
                modifier = Modifier.padding(padding),
                navController = navController,
                favoriteSatellites = favoriteSatellites,
                onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
        }
    }
}

@Composable
fun HomeTabContent(
    modifier: Modifier = Modifier,
    userLocation: Location? = null,
    onRefreshLocation: () -> Unit = {},
    totalSatellites: Int = 0,
    favoriteCount: Int = 0,
    onNavigateToSatellites: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToMap: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Location Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "شوێنی تۆ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onRefreshLocation) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (userLocation != null) {
                    Column {
                        LocationInfoRow(
                            label = "پانی",
                            value = String.format(Locale.US, "%.4f°", userLocation.latitude)
                        )
                        LocationInfoRow(
                            label = "درێژی",
                            value = String.format(Locale.US, "%.4f°", userLocation.longitude)
                        )
                        if (userLocation.hasAltitude()) {
                            LocationInfoRow(
                                label = "بەرزی",
                                value = String.format(Locale.US, "%.1f م", userLocation.altitude)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "شوێنەکە بەدەست نەهێنرا",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Satellite,
                title = "کۆی مانگەکان",
                value = totalSatellites.toString(),
                color = MaterialTheme.colorScheme.primary
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Favorite,
                title = "دڵخوازەکان",
                value = favoriteCount.toString(),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Actions
        Text(
            text = "کرداری خێرا",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                text = "هەموو مانگەکان",
                onClick = onNavigateToSatellites
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                text = "دڵخوازەکان",
                onClick = onNavigateToFavorites
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        QuickActionButton(
            modifier = Modifier.fillMaxWidth(),
            text = "نەخشەی شوێن",
            onClick = onNavigateToMap
        )
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun LocationInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SatellitesTabContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    satellites: List<Satellite>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavorite: (Satellite) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(KurdishStrings.SEARCH_SATELLITE) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )
        
        if (satellites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = KurdishStrings.NO_SATELLITES_FOUND)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(satellites) { satellite ->
                    SatelliteItem(
                        satellite = satellite,
                        onClick = { navController.navigate(Screen.Detail.createRoute(satellite.id)) },
                        onToggleFavorite = { onToggleFavorite(satellite) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesTabContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    favoriteSatellites: List<Satellite>,
    onToggleFavorite: (Satellite) -> Unit
) {
    if (favoriteSatellites.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "هیچ مانگێکی دەستکرد لە دڵخوازەکاندا نییە",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favoriteSatellites) { satellite ->
                SatelliteItem(
                    satellite = satellite,
                    onClick = { navController.navigate(Screen.Detail.createRoute(satellite.id)) },
                    onToggleFavorite = { onToggleFavorite(satellite) }
                )
            }
        }
    }
}

@Composable
fun SatelliteItem(
    satellite: Satellite,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = satellite.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${satellite.longitude}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (satellite.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (satellite.isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    }
}
