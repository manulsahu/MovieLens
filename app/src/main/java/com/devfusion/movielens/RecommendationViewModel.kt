package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecommendationViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val movieRepository = MovieRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadRecommendations()
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            try {
                // Step 1: Get user's watched movies from MyMovies tab
                val userWatchedMovies = getUserWatchedMovies(userId)

                // Step 2: Get user's watchlist from MyMovies tab
                val userWatchlist = getUserWatchlist(userId)

                // Step 3: Get new releases (always show these)
                val newReleases = movieRepository.getNewReleases()

                // Step 4: Generate personalized recommendations
                val personalizedRecommendations = if (userWatchedMovies.isNotEmpty() || userWatchlist.isNotEmpty()) {
                    generateRecommendationsFromUserMovies(userWatchedMovies + userWatchlist)
                } else {
                    // If no user data, show popular movies
                    movieRepository.getPopularMovies()
                }

                // Step 5: Get upcoming movies
                val upcomingMovies = movieRepository.getUpcomingMovies()

                _uiState.value = HomeUiState(
                    userName = getCurrentUserName(),
                    watchHistory = userWatchedMovies.take(6),
                    recommendations = personalizedRecommendations,
                    newReleases = newReleases,
                    upcomingMovies = upcomingMovies
                )
            } catch (e: Exception) {
                // Fallback if anything fails
                val popularMovies = movieRepository.getPopularMovies()
                val newReleases = movieRepository.getNewReleases()

                _uiState.value = HomeUiState(
                    userName = getCurrentUserName(),
                    watchHistory = emptyList(),
                    recommendations = popularMovies,
                    newReleases = newReleases,
                    upcomingMovies = emptyList()
                )
            }
        }
    }

    private suspend fun getUserWatchedMovies(userId: String): List<Movie> {
        return try {
            db.collection("user_movies")
                .whereEqualTo("userId", userId)
                .whereEqualTo("watched", true)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(UserMovie::class.java) }
                .map { it.toMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun getUserWatchlist(userId: String): List<Movie> {
        return try {
            db.collection("user_movies")
                .whereEqualTo("userId", userId)
                .whereEqualTo("watchlist", true)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(UserMovie::class.java) }
                .map { it.toMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun generateRecommendationsFromUserMovies(userMovies: List<Movie>): List<Movie> {
        if (userMovies.isEmpty()) return emptyList()

        // Analyze user preferences
        val userPreferences = analyzeUserPreferences(userMovies)

        // Get similar movies from TMDB based on user preferences
        return movieRepository.getSimilarMovies(userPreferences)
    }

    private fun analyzeUserPreferences(userMovies: List<Movie>): UserPreferences {
        // Extract favorite genres from user's movies
        val favoriteGenres = userMovies
            .flatMap { it.genres ?: emptyList() }
            .groupBy { it }
            .mapValues { it.value.size }
            .entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }

        // Calculate average preferred rating
        val averageRating = if (userMovies.isNotEmpty()) {
            userMovies.mapNotNull { it.voteAverage }.average()
        } else {
            7.0 // Default average rating
        }

        // Calculate preferred years
        val years = userMovies.mapNotNull { it.releaseDate?.take(4)?.toIntOrNull() }
        val preferredYears = if (years.isNotEmpty()) {
            val avgYear = years.average().toInt()
            (avgYear - 5)..(avgYear + 5)
        } else {
            2000..2024
        }

        return UserPreferences(
            favoriteGenres = favoriteGenres,
            preferredRatingRange = (averageRating - 1.0)..(averageRating + 1.0),
            preferredYears = preferredYears
        )
    }

    fun addToWatchHistory(movie: Movie) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

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

            db.collection("user_movies")
                .add(userMovie)
                .addOnSuccessListener {
                    loadRecommendations() // Refresh recommendations
                }
        }
    }

    fun addToWatchlist(movie: Movie) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

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

            db.collection("user_movies")
                .add(userMovie)
                .addOnSuccessListener {
                    loadRecommendations() // Refresh recommendations
                }
        }
    }

    fun refreshRecommendations() {
        loadRecommendations()
    }

    private fun getCurrentUserName(): String {
        val user = auth.currentUser
        return user?.displayName ?: (user?.email?.substringBefore('@') ?: "Movie Lover")
    }
}