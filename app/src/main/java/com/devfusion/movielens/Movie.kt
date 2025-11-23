package com.devfusion.movielens

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
    val voteAverage: Double? = null,
    val genres: List<String> = emptyList()
)

/**
 * UserMovie for Firestore storage - no Room annotations
 */
data class UserMovie(
    val userId: String = "", // Associate with user account
    val movieId: Int = 0, // The actual movie ID from TMDB/API
    val title: String = "",
    val posterPath: String? = null,
    val overview: String? = null,
    val releaseDate: String? = null,
    val voteAverage: Double? = null,
    val genres: List<String> = emptyList(),
    val watched: Boolean = false,
    val watchlist: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toMovie(): Movie {
        return Movie(
            id = movieId,
            title = title,
            posterPath = posterPath,
            overview = overview,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            genres = genres
        )
    }
}