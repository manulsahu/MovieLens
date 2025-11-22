package com.devfusion.movielens

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

// DTOs only for the fields we need
data class TmdbPagedResponse(
    val page: Int,
    val results: List<TmdbMovieItem>
)

data class TmdbMovieItem(
    val id: Long,
    val title: String?,
    val name: String?, // for TV (not used heavily here)
    val poster_path: String?,
    val release_date: String?,
    val genre_ids: List<Int>?
)

interface TmdbService {
    // we'll call discover endpoint with provider filtering for streaming platforms
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") providerId: Int,
        @Query("watch_region") region: String = "US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse>

    @GET("movie/popular")
    suspend fun popularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse>

    companion object {
        private const val BASE = "https://api.themoviedb.org/3/"

        fun create(): TmdbService {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TmdbService::class.java)
        }
    }
}

/** helper: build full poster url */
fun tmdbPosterUrl(path: String?): String? {
    if (path == null) return null
    // common base path - w500 gives nice size for posters
    return "https://image.tmdb.org/t/p/w500$path"
}
