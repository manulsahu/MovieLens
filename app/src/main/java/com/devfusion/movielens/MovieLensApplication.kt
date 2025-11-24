package com.devfusion.movielens

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieLensApplication : Application() {
    lateinit var authManager: AuthManager

    override fun onCreate() {
        super.onCreate()
        authManager = AuthManager(this)
    }
}