package com.devfusion.movielens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMoviesScreen(
    modifier: Modifier = Modifier,
    viewModel: MyMoviesViewModel = viewModel()
) {
    // Dialog visibility state
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add movie")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("To Be Watched", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.toBeWatched.isEmpty()) {
                Text("No movies added to your to-be-watched list.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.toBeWatched, key = { it.id }) { movie ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(movie.title)
                                Text("${movie.platform ?: ""} • ${movie.genre ?: ""}", style = MaterialTheme.typography.bodySmall)
                            }

                            // Mark as watched button
                            IconButton(onClick = { viewModel.markAsWatched(movie) }) {
                                Icon(Icons.Default.Done, contentDescription = "Mark watched")
                            }

                            // Remove from to-be-watched
                            IconButton(onClick = { viewModel.removeFromToBeWatched(movie) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("My Movies (Watched)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.watched.isEmpty()) {
                Text("You haven't marked any movies as watched yet.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.watched, key = { it.id }) { movie ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(movie.title)
                                Text("${movie.platform ?: ""} • ${movie.genre ?: ""}", style = MaterialTheme.typography.bodySmall)
                            }

                            // optional remove button for watched
                            IconButton(onClick = { viewModel.removeFromWatched(movie) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMovieDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, category, selectedGenres, addToWatched ->
                val id = UUID.randomUUID().toString()
                val movie = Movie(
                    id = id,
                    title = title,
                    genre = selectedGenres.joinToString(", "),
                    platform = category,
                    posterUrl = null,
                    releaseYear = null
                )
                if (addToWatched) viewModel.addWatched(movie) else viewModel.addToBeWatched(movie)
                showAddDialog = false
            }
        )
    }
}
