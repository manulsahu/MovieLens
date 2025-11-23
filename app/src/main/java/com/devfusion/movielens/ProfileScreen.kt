package com.devfusion.movielens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    var showEdit by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    val photoUrl: Uri? = user?.photoUrl

    // Observe the user's display name from ViewModel
    LaunchedEffect(user?.uid) {
        user?.displayName?.let { name ->
            displayName = name
        }
    }

    if (showEdit) {
        EditNameDialog(
            initial = displayName,
            isLoading = isLoading,
            onSave = { newName ->
                isLoading = true
                viewModel.updateDisplayName(newName) { success ->
                    isLoading = false
                    if (success) {
                        displayName = newName
                    }
                    showEdit = false
                }
            },
            onCancel = { showEdit = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        // User Info Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Picture
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // User Details
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            displayName.ifBlank { "Anonymous User" },
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            user?.email ?: "No email",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (user?.isEmailVerified == true) {
                            Text(
                                "✓ Email verified",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Edit Profile Button
                OutlinedButton(
                    onClick = { showEdit = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit profile"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Display Name")
                }
            }
        }

        // Stats Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Your Movie Stats",
                    style = MaterialTheme.typography.titleMedium
                )

                // You can add actual stats here later from ViewModel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("To Watch", style = MaterialTheme.typography.bodyMedium)
                        Text("0", style = MaterialTheme.typography.titleMedium) // Replace with actual count
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Watched", style = MaterialTheme.typography.bodyMedium)
                        Text("0", style = MaterialTheme.typography.titleMedium) // Replace with actual count
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total", style = MaterialTheme.typography.bodyMedium)
                        Text("0", style = MaterialTheme.typography.titleMedium) // Replace with actual count
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = {
                viewModel.logout()
                val intent = Intent(context, SignInActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@Composable
fun EditNameDialog(
    initial: String,
    isLoading: Boolean,
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onCancel() },
        title = { Text("Edit Display Name") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name) },
                enabled = name.isNotBlank() && !isLoading
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}