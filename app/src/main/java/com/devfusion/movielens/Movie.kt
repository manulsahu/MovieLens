package com.devfusion.movielens

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single canonical Movie model used across the app.
 * All fields that may be absent are nullable.
 */
data class Movie(
    val id: Int, // Changed from String to Int to match TMDB
    val title: String,
    val posterPath: String? = null,
    val overview: String? = null,
    val releaseDate: String? = null,
    val voteAverage: Double? = null
)

@Entity(tableName = "user_movies")
data class UserMovie(
    @PrimaryKey
    val id: String,
    val userId: String, // Associate with user account
    val movieId: Int, // The actual movie ID from TMDB/API
    val title: String,
    val posterPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val isWatched: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)