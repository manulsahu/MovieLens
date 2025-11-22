package com.devfusion.movielens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TrendingScreen(
    modifier: Modifier = Modifier,
    viewModel: TrendingViewModel = viewModel()
) {
    // collectAsState needs the import above; gives you a Compose State from the StateFlow
    val uiState by viewModel.uiState.collectAsState()

    val platforms = listOf("Netflix", "Amazon Prime", "MX Player")

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Trending", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        // simple platform chips row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            platforms.forEach { p ->
                FilterChip(
                    selected = (p == uiState.platform),
                    onClick = { viewModel.loadForPlatform(p) },
                    label = { Text(p) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.loading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // ensure uiState.items is a List<Movie>
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // use a stable key so Compose can track items
                items(uiState.items, key = { it.id }) { movie ->
                    TrendingRow(movie)
                }
            }
        }
    }
}

@Composable
fun TrendingRow(movie: Movie) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster placeholder (swap with AsyncImage/Coil later if you add Coil)
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 88.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                // If posterUrl exists and you add Coil/Accompanist, replace this Text with AsyncImage.
                Text(text = "Poster", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
                val year = movie.releaseYear ?: ""
                val genre = movie.genre ?: ""
                Text(
                    text = listOfNotNull(year.takeIf { it.isNotBlank() }, genre.takeIf { it.isNotBlank() }).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(movie.platform ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
