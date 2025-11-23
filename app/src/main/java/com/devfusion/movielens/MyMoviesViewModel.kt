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

    fun loadUserMovies() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            _watchedMovies.value = userMoviesRepository.getWatchedMovies(userId)
            _watchlistMovies.value = userMoviesRepository.getWatchlistMovies(userId)
        }
    }

    suspend fun addToWatched(movie: Movie) {
        val userId = auth.currentUser?.uid ?: return
        userMoviesRepository.addToWatched(userId, movie)
        loadUserMovies() // Refresh the list
    }

    suspend fun addToWatchlist(movie: Movie) {
        val userId = auth.currentUser?.uid ?: return
        userMoviesRepository.addToWatchlist(userId, movie)
        loadUserMovies() // Refresh the list
    }

    suspend fun markAsWatched(userMovie: UserMovie) {
        val userId = auth.currentUser?.uid ?: return
        userMoviesRepository.markAsWatched(userId, userMovie.movieId)
        loadUserMovies() // Refresh the list
    }

    suspend fun removeFromWatchlist(movieId: Int) {
        val userId = auth.currentUser?.uid ?: return
        userMoviesRepository.removeFromWatchlist(userId, movieId)
        loadUserMovies() // Refresh the list
    }

    suspend fun removeFromWatched(movieId: Int) {
        val userId = auth.currentUser?.uid ?: return
        // For watched movies, we can delete them entirely or move to watchlist
        // Currently deleting them
        userMoviesRepository.removeFromWatchlist(userId, movieId) // Same method works for both
        loadUserMovies() // Refresh the list
    }
}