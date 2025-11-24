package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileViewModel : ViewModel() {

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

    fun logout(authManager: AuthManager) {
        authManager.logout()
    }

    fun getCurrentUserId(authManager: AuthManager): String? {
        return authManager.getUserId()
    }
}