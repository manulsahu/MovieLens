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
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    var showEdit by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    val photoUrl: Uri? = user?.photoUrl

    if (showEdit) {
        EditNameDialog(initial = displayName, onSave = { newName ->
            // update Firebase
            val req = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
            user?.updateProfile(req)?.addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    displayName = newName
                }
                showEdit = false
            }
        }, onCancel = { showEdit = false })
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (photoUrl != null) {
                AsyncImage(model = photoUrl, contentDescription = "Avatar", modifier = Modifier.size(72.dp).clip(CircleShape))
            } else {
                Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(displayName.ifBlank { "Anonymous" }, style = MaterialTheme.typography.titleMedium)
                Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }

        OutlinedButton(onClick = { showEdit = true }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit profile")
        }

        Button(onClick = { /* settings later */ }) {
            Text("Settings")
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(onClick = {
            FirebaseAuth.getInstance().signOut()
            ctx.startActivity(Intent(ctx, SignInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }) {
            Icon(Icons.Default.Logout, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@Composable
fun EditNameDialog(initial: String, onSave: (String) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Edit name") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } }
    )
}
