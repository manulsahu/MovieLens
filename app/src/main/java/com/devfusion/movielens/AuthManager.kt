package com.devfusion.movielens

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUserId = MutableStateFlow(getCurrentUserId())
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow() // FIXED: This is already public

    fun login(userId: String, rememberMe: Boolean = false) {
        if (rememberMe) {
            // Save login credentials for auto-login
            sharedPreferences.edit()
                .putString("user_id", userId)
                .putBoolean("is_logged_in", true)
                .putBoolean("remember_me", true)
                .apply()
        } else {
            // Only save session for current app instance
            sharedPreferences.edit()
                .putBoolean("is_logged_in", true)
                .putBoolean("remember_me", false)
                .apply()
        }

        _isLoggedIn.value = true
        _currentUserId.value = userId
    }

    fun logout() {
        sharedPreferences.edit()
            .remove("is_logged_in")
            .remove("user_id")
            .remove("remember_me")
            .apply()

        _isLoggedIn.value = false
        _currentUserId.value = null
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    private fun getCurrentUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun shouldRememberMe(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }

    fun getRememberedUserId(): String? {
        return if (shouldRememberMe()) {
            sharedPreferences.getString("user_id", null)
        } else {
            null
        }
    }

    // ADD THIS METHOD FOR EASY ACCESS TO CURRENT USER ID
    fun getUserId(): String? {
        return _currentUserId.value
    }
}