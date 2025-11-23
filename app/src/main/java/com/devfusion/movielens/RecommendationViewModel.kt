package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

data class HomeUiState(
    val userName: String = "User",
    val watchHistory: List<Movie> = emptyList(),
    val recommendations: List<Movie> = emptyList()
)

class RecommendationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(300)

            val recommendations = listOf(
                Movie(1, "Interstellar", "/bR4U9n9ENjYAbCqBgxOQH3mOx4n.jpg", releaseDate = "2014", voteAverage = 8.3),
                Movie(2, "The Martian", "/5aGhaIHYuQbqlHWvWYqMCnj40y2.jpg", releaseDate = "2015", voteAverage = 7.7),
                Movie(3, "Inception", "/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg", releaseDate = "2010", voteAverage = 8.4)
            )

            val watchHistory = listOf(
                Movie(4, "John Wick", "/fZPSd91yGE9fCcCe6OoQr6E3Bev.jpg", releaseDate = "2014", voteAverage = 7.4),
                Movie(5, "La La Land", "/uDO8zWDhfWwoFdKS4fzkUJt0Rf0.jpg", releaseDate = "2016", voteAverage = 8.0),
                Movie(6, "Arrival", "/hLudzvGfpi6JlwUnsNhXwKKg4j.jpg", releaseDate = "2016", voteAverage = 7.9)
            )

            _uiState.value = HomeUiState(
                userName = getCurrentUserName(),
                watchHistory = watchHistory,
                recommendations = recommendations
            )
        }
    }

    private fun getCurrentUserName(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName ?: (user?.email?.substringBefore('@') ?: "Movie Lover")
    }
}