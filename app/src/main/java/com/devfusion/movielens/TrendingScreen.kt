package com.devfusion.movielens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun TrendingScreen(
    modifier: Modifier = Modifier,
    viewModel: TrendingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val platforms = listOf("Netflix", "MX Player")

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Trending", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
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
        shape = RoundedCornerShape(8.dp),
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
            // Simple poster image with Coil - using posterPath instead of posterUrl
            if (!movie.posterPath.isNullOrBlank()) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = "${movie.title} poster",
                    modifier = Modifier
                        .size(width = 64.dp, height = 88.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback placeholder if no poster URL
                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 88.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clip(RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Poster",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Updated to use releaseDate and voteAverage instead of releaseYear and genre
                val year = movie.releaseDate?.take(4) ?: ""
                val rating = movie.voteAverage?.let { "⭐ ${String.format("%.1f", it)}" } ?: ""
                Text(
                    text = listOfNotNull(
                        year.takeIf { it.isNotBlank() },
                        rating.takeIf { it.isNotBlank() }
                    ).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))

                // Show overview snippet if available
                movie.overview?.let { overview ->
                    Text(
                        text = overview.take(60) + if (overview.length > 60) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Add this annotation to suppress the experimental API warning
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = label
    )
}