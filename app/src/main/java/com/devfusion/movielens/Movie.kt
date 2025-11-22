package com.devfusion.movielens

/**
 * Single canonical Movie model used across the app.
 * All fields that may be absent are nullable.
 */
data class Movie(
    val id: String,
    val title: String,
    val genre: String? = null,
    val platform: String? = null,
    val posterUrl: String? = null,
    val releaseYear: String? = null
)