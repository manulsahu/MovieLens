package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import com.devfusion.movielens.auth.AuthManager
import com.devfusion.movielens.repository.UserMoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMoviesViewModel @Inject constructor(
    private val repository: UserMoviesRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _toBeWatched = mutableStateListOf<Movie>()
    val toBeWatched: List<Movie> get() = _toBeWatched

    private val _watched = mutableStateListOf<Movie>()
    val watched: List<Movie> get() = _watched

    private var toBeWatchedJob: Job? = null
    private var watchedJob: Job? = null

    init {
        loadUserMovies()
    }

    private fun loadUserMovies() {
        val currentUserId = authManager.getCurrentUserId()
        if (currentUserId.isEmpty()) {
            _toBeWatched.clear()
            _watched.clear()
            return
        }

        // Cancel previous jobs
        toBeWatchedJob?.cancel()
        watchedJob?.cancel()

        // Load to-be-watched movies
        toBeWatchedJob = repository.getToBeWatchedMovies(currentUserId)
            .onEach { userMovies ->
                _toBeWatched.clear()
                _toBeWatched.addAll(userMovies.map { it.toMovie() })
            }
            .launchIn(viewModelScope)

        // Load watched movies
        watchedJob = repository.getWatchedMovies(currentUserId)
            .onEach { userMovies ->
                _watched.clear()
                _watched.addAll(userMovies.map { it.toMovie() })
            }
            .launchIn(viewModelScope)
    }

    fun addToBeWatched(movie: Movie) {
        viewModelScope.launch {
            val userId = authManager.getCurrentUserId()
            if (userId.isNotEmpty()) {
                repository.addToBeWatched(userId, movie)
            }
        }
    }

    fun addWatched(movie: Movie) {
        viewModelScope.launch {
            val userId = authManager.getCurrentUserId()
            if (userId.isNotEmpty()) {
                repository.addToWatched(userId, movie)
            }
        }
    }

    fun markAsWatched(movie: Movie) {
        viewModelScope.launch {
            val userId = authManager.getCurrentUserId()
            if (userId.isNotEmpty()) {
                repository.markAsWatched(userId, movie)
            }
        }
    }

    fun removeFromToBeWatched(movie: Movie) {
        viewModelScope.launch {
            val userId = authManager.getCurrentUserId()
            if (userId.isNotEmpty()) {
                repository.removeFromToBeWatched(userId, movie)
            }
        }
    }

    fun removeFromWatched(movie: Movie) {
        viewModelScope.launch {
            val userId = authManager.getCurrentUserId()
            if (userId.isNotEmpty()) {
                repository.removeFromWatched(userId, movie)
            }
        }
    }

    fun refreshMovies() {
        loadUserMovies()
    }

    fun clearAll() {
        _toBeWatched.clear()
        _watched.clear()
    }
}

// Extension function to convert UserMovie to Movie
fun UserMovie.toMovie(): Movie {
    return Movie(
        id = this.movieId,
        title = this.title,
        posterPath = this.posterPath,
        overview = this.overview,
        releaseDate = this.releaseDate,
        voteAverage = this.voteAverage
    )
}