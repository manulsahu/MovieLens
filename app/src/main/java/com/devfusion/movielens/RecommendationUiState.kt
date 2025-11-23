package com.devfusion.movielens

data class RecommendationUiState(
    val loading: Boolean = true,
    val userName: String = "User",
    val recommendations: List<Movie> = emptyList(),
    val watchHistory: List<Movie> = emptyList()
)