package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class MyMoviesViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userMoviesRepository = UserMoviesRepository()

    private val _watchedMovies = MutableStateFlow<List<UserMovie>>(emptyList())
    val watchedMovies: StateFlow<List<UserMovie>> = _watchedMovies

    private val _watchlistMovies = MutableStateFlow<List<UserMovie>>(emptyList())
    val watchlistMovies: StateFlow<List<UserMovie>> = _watchlistMovies

    private val _movieStats = MutableStateFlow(MovieStats(0, 0, 0))
    val movieStats: StateFlow<MovieStats> = _movieStats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    data class MovieStats(
        val toWatchCount: Int,
        val watchedCount: Int,
        val totalCount: Int
    )

    fun loadUserMovies() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            _isLoading.value = true
            try {
                val watched = userMoviesRepository.getWatchedMovies(userId)
                val watchlist = userMoviesRepository.getWatchlistMovies(userId)

                _watchedMovies.value = watched
                _watchlistMovies.value = watchlist

                _movieStats.value = MovieStats(
                    toWatchCount = watchlist.size,
                    watchedCount = watched.size,
                    totalCount = watchlist.size + watched.size
                )
            } catch (e: Exception) {
                // Handle error appropriately
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun addToWatched(movie: Movie) {
        val userId = auth.currentUser?.uid ?: return
        try {
            userMoviesRepository.addToWatched(userId, movie)
            loadUserMovies()
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
    }

    suspend fun addToWatchlist(movie: Movie) {
        val userId = auth.currentUser?.uid ?: return
        try {
            userMoviesRepository.addToWatchlist(userId, movie)
            loadUserMovies()
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
    }

    suspend fun markAsWatched(userMovie: UserMovie) {
        val userId = auth.currentUser?.uid ?: return
        try {
            userMoviesRepository.markAsWatched(userId, userMovie.movieId)
            loadUserMovies()
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
    }

    suspend fun removeFromWatchlist(movieId: Int) {
        val userId = auth.currentUser?.uid ?: return
        try {
            userMoviesRepository.removeFromWatchlist(userId, movieId)
            loadUserMovies()
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
    }

    suspend fun removeFromWatched(movieId: Int) {
        val userId = auth.currentUser?.uid ?: return
        try {
            userMoviesRepository.removeFromWatched(userId, movieId)
            loadUserMovies()
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
    }
}