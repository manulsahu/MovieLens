package com.devfusion.movielens.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.devfusion.movielens.auth.AuthManager
import com.devfusion.movielens.data.AppDatabase
import com.devfusion.movielens.data.UserMoviesDao
import com.devfusion.movielens.repository.UserMoviesRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideUserMoviesDao(database: AppDatabase): UserMoviesDao {
        return database.userMoviesDao()
    }

    @Provides
    @Singleton
    fun provideUserMoviesRepository(userMoviesDao: UserMoviesDao): UserMoviesRepository {
        return UserMoviesRepository(userMoviesDao)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("movie_lens_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthManager(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences,
        firebaseAuth: FirebaseAuth
    ): AuthManager {
        return AuthManager(context, sharedPreferences, firebaseAuth)
    }
}