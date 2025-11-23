package com.devfusion.movielens

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserMoviesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("user_movies")

    suspend fun getWatchedMovies(userId: String): List<UserMovie> {
        return try {
            collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("watched", true)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(UserMovie::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWatchlistMovies(userId: String): List<UserMovie> {
        return try {
            collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("watchlist", true)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(UserMovie::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToWatched(userId: String, movie: Movie) {
        try {
            val userMovie = UserMovie(
                userId = userId,
                movieId = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage,
                genres = movie.genres,
                watched = true,
                watchlist = false
            )

            collection.add(userMovie).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun addToWatchlist(userId: String, movie: Movie) {
        try {
            val userMovie = UserMovie(
                userId = userId,
                movieId = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage,
                genres = movie.genres,
                watched = false,
                watchlist = true
            )

            collection.add(userMovie).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun removeFromWatchlist(userId: String, movieId: Int) {
        try {
            val query = collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("watchlist", true)
                .get()
                .await()

            query.documents.forEach { document ->
                document.reference.delete().await()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun markAsWatched(userId: String, movieId: Int) {
        try {
            val query = collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("movieId", movieId)
                .get()
                .await()

            query.documents.forEach { document ->
                document.reference.update("watched", true, "watchlist", false).await()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}