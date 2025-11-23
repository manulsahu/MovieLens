package com.devfusion.movielens.data

import androidx.room.*
import com.devfusion.movielens.UserMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMoviesDao {
    @Query("SELECT * FROM user_movies WHERE userId = :userId AND isWatched = 0")
    fun getToBeWatchedMovies(userId: String): Flow<List<UserMovie>>

    @Query("SELECT * FROM user_movies WHERE userId = :userId AND isWatched = 1")
    fun getWatchedMovies(userId: String): Flow<List<UserMovie>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: UserMovie)

    @Query("UPDATE user_movies SET isWatched = :isWatched WHERE id = :movieId AND userId = :userId")
    suspend fun updateWatchedStatus(userId: String, movieId: String, isWatched: Boolean)

    @Query("DELETE FROM user_movies WHERE id = :movieId AND userId = :userId")
    suspend fun deleteMovie(userId: String, movieId: String)

    @Query("DELETE FROM user_movies WHERE userId = :userId")
    suspend fun clearUserMovies(userId: String)
}