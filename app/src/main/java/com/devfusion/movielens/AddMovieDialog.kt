package com.devfusion.movielens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, genres: List<String>, addToWatched: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var selectedGenres by remember { mutableStateOf(listOf<String>()) }
    var addToWatched by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Movie") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Movie title input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Movie name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { /* menu auto handled below */ }
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )

                    LaunchedEffect(Unit) { expanded = false }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    category = it
                                    expanded = false
                                }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.dp)
                            .clickable { expanded = true }
                    )
                }

                // Genre selection (multi-select chips)
                Text("Genres")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(genresList) { g ->
                        val selected = selectedGenres.contains(g)
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedGenres =
                                    if (selected) selectedGenres - g else selectedGenres + g
                            },
                            label = { Text(g) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }

                // Add to watched toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = addToWatched,
                        onCheckedChange = { addToWatched = it }
                    )
                    Text("Mark as watched")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, category, selectedGenres, addToWatched)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Constants
val categories = listOf("Bollywood", "Tollywood", "Hollywood")
val genresList = listOf(
    "Love", "Romance", "Comedy", "Action", "Thriller",
    "Horror", "Drama", "Sci-Fi", "Fantasy", "Adventure"
)
