package com.devfusion.movielens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devfusion.movielens.ui.theme.MovieLensTheme

class MainActivity : ComponentActivity() {
    private val authManager: AuthManager by lazy {
        (application as MovieLensApplication).authManager // FIXED: lowercase 'authManager'
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MovieLensTheme {
                val isLoggedIn by authManager.isLoggedIn.collectAsStateWithLifecycle()

                LaunchedEffect(isLoggedIn) {
                    if (!isLoggedIn) {
                        // Navigate to SignInActivity if not logged in
                        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                        finish()
                    }
                }

                if (isLoggedIn) {
                    MovieLensApp()
                }
            }
        }
    }
}