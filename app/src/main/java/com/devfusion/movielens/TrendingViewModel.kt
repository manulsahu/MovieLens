package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TrendingUiState(
    val loading: Boolean = true,
    val platform: String = "Netflix",
    val items: List<Movie> = emptyList()
)

class TrendingViewModel(
    private val tmdb: TmdbService = TmdbService.create(),

    // SAFE way to read BuildConfig.TMDB_API_KEY
    private val tmdbApiKey: String? = try {
        BuildConfig::class.java.getField("TMDB_API_KEY").get(null) as? String
    } catch (t: Throwable) {
        null
    }
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState

    private val providerIds = mapOf(
        "Netflix" to 8,
//        "Amazon Prime" to 119,
        "MX Player" to 387
    )

    init {
        loadForPlatform(_uiState.value.platform)
    }

    fun loadForPlatform(platform: String) {
        val providerId = providerIds[platform] ?: providerIds.values.first()

        _uiState.value = _uiState.value.copy(
            loading = true,
            platform = platform
        )

        val key = tmdbApiKey?.takeIf { it.isNotBlank() }
        if (key == null) {
            _uiState.value = _uiState.value.copy(loading = false, items = emptyList())
            return
        }

        viewModelScope.launch {
            try {
                val resp = tmdb.discoverMovies(
                    apiKey = key,
                    providerId = providerId,
                    page = 1
                )

                if (resp.isSuccessful) {
                    val body = resp.body()

                    val movies = (body?.results ?: emptyList()).take(10).map { item ->
                        Movie(
                            id = item.id.toInt(), // Convert Long to Int
                            title = (item.title ?: item.name).orEmpty(),
                            posterPath = item.poster_path,
                            overview = null, // Your TmdbMovieItem doesn't have overview
                            releaseDate = item.release_date,
                            voteAverage = null // Your TmdbMovieItem doesn't have vote_average
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        items = movies
                    )
                } else {
                    // Fallback to sample data if API fails
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        items = getSampleMovies(platform)
                    )
                }

            } catch (t: Throwable) {
                t.printStackTrace()
                // Fallback to sample data on error
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    items = getSampleMovies(platform)
                )
            }
        }
    }

    private fun getSampleMovies(platform: String): List<Movie> {
        return listOf(
            Movie(
                1,
                "The Matrix",
                "/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg",
                "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
                "1999-03-31",
                8.7
            ),
            Movie(
                2,
                "Inception",
                "/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
                "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
                "2010-07-16",
                8.4
            ),
            Movie(
                3,
                "Interstellar",
                "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
                "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                "2014-11-07",
                8.6
            )
        )
    }
}