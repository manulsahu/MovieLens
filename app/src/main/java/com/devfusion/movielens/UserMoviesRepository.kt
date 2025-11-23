package com.devfusion.movielens.repository

import com.devfusion.movielens.Movie
import com.devfusion.movielens.UserMovie
import com.devfusion.movielens.data.UserMoviesDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserMoviesRepository @Inject constructor(
    private val userMoviesDao: UserMoviesDao
) {

    fun getToBeWatchedMovies(userId: String): Flow<List<UserMovie>> {
        return userMoviesDao.getToBeWatchedMovies(userId)
    }

    fun getWatchedMovies(userId: String): Flow<List<UserMovie>> {
        return userMoviesDao.getWatchedMovies(userId)
    }

    suspend fun addToBeWatched(userId: String, movie: Movie) {
        val userMovie = UserMovie(
            id = "${userId}_${movie.id}",
            userId = userId,
            movieId = movie.id,
            title = movie.title,
            posterPath = movie.posterPath,
            overview = movie.overview,
            releaseDate = movie.releaseDate,
            voteAverage = movie.voteAverage,
            isWatched = false
        )
        userMoviesDao.insertMovie(userMovie)
    }

    suspend fun addToWatched(userId: String, movie: Movie) {
        val userMovie = UserMovie(
            id = "${userId}_${movie.id}",
            userId = userId,
            movieId = movie.id,
            title = movie.title,
            posterPath = movie.posterPath,
            overview = movie.overview,
            releaseDate = movie.releaseDate,
            voteAverage = movie.voteAverage,
            isWatched = true
        )
        userMoviesDao.insertMovie(userMovie)
    }

    suspend fun markAsWatched(userId: String, movie: Movie) {
        userMoviesDao.updateWatchedStatus(userId, "${userId}_${movie.id}", true)
    }

    suspend fun removeFromToBeWatched(userId: String, movie: Movie) {
        userMoviesDao.deleteMovie(userId, "${userId}_${movie.id}")
    }

    suspend fun removeFromWatched(userId: String, movie: Movie) {
        userMoviesDao.deleteMovie(userId, "${userId}_${movie.id}")
    }
}