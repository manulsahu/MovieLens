package com.devfusion.movielens.auth

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) {

    fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun setCurrentUserId(userId: String) {
        sharedPreferences.edit().putString("current_user_id", userId).apply()
    }

    fun logout() {
        firebaseAuth.signOut()
        sharedPreferences.edit().remove("current_user_id").apply()
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}