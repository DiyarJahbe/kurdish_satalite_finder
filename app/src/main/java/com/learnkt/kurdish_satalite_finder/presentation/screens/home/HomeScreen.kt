package com.learnkt.kurdish_satalite_finder.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Satellite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.learnkt.kurdish_satalite_finder.core.localization.KurdishStrings
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.presentation.navigation.Screen

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
                onNavigateToSatellites = { selectedTab = "satellites" }
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
    onNavigateToSatellites: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Satellite,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = KurdishStrings.APP_NAME,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "مانگە دەستکردەکان بدۆزەرەوە و ڕێکبخە",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNavigateToSatellites,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("دەست پێ بکە")
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
