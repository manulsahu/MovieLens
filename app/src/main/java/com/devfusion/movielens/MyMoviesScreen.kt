package com.devfusion.movielens

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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMoviesScreen(
    modifier: Modifier = Modifier,
    viewModel: MyMoviesViewModel = viewModel()
) {
    // Dialog visibility state
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Collect state from ViewModel
    val watchedMovies by viewModel.watchedMovies.collectAsState()
    val watchlistMovies by viewModel.watchlistMovies.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserMovies()
    }

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

            if (watchlistMovies.isEmpty()) {
                Text("No movies added to your to-be-watched list.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(watchlistMovies, key = { it.movieId }) { userMovie ->
                        val movie = userMovie.toMovie()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(movie.title)
                                Text(
                                    "${movie.releaseDate?.take(4) ?: "Unknown"} • ⭐ ${movie.voteAverage ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Mark as watched button
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.markAsWatched(userMovie)
                                }
                            }) {
                                Icon(Icons.Default.Done, contentDescription = "Mark watched")
                            }

                            // Remove from to-be-watched
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.removeFromWatchlist(userMovie.movieId)
                                }
                            }) {
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

            if (watchedMovies.isEmpty()) {
                Text("You haven't marked any movies as watched yet.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(watchedMovies, key = { it.movieId }) { userMovie ->
                        val movie = userMovie.toMovie()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(movie.title)
                                Text(
                                    "${movie.releaseDate?.take(4) ?: "Unknown"} • ⭐ ${movie.voteAverage ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Remove button for watched movies
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.removeFromWatched(userMovie.movieId)
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        // Simple text input dialog for adding movies manually
        var movieTitle by remember { mutableStateOf("") }
        var addToWatched by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Movie") },
            text = {
                Column {
                    OutlinedTextField(
                        value = movieTitle,
                        onValueChange = { movieTitle = it },
                        label = { Text("Movie Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add as watched?")
                        Switch(
                            checked = addToWatched,
                            onCheckedChange = { addToWatched = it }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (movieTitle.isNotBlank()) {
                            val movie = Movie(
                                id = System.currentTimeMillis().toInt(), // Use timestamp as ID for manual entries
                                title = movieTitle,
                                posterPath = null,
                                releaseDate = null,
                                voteAverage = null
                            )
                            coroutineScope.launch {
                                if (addToWatched) {
                                    viewModel.addToWatched(movie)
                                } else {
                                    viewModel.addToWatchlist(movie)
                                }
                            }
                            showAddDialog = false
                        }
                    },
                    enabled = movieTitle.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}