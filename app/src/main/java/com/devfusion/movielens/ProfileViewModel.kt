package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import com.devfusion.movielens.auth.AuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    fun updateDisplayName(newName: String, onComplete: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        } else {
            onComplete(false)
        }
    }

    fun logout() {
        authManager.logout()
    }

    fun getCurrentUserId(): String {
        return authManager.getCurrentUserId()
    }
}