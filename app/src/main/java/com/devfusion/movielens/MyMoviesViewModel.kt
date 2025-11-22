package com.devfusion.movielens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel for MyMovies screen.
 * Keeps lists of "to be watched" and "watched" as SnapshotStateList so Composables update automatically.
 */
class MyMoviesViewModel : ViewModel() {

    // use mutableStateListOf so the UI observes changes (do NOT wrap these in rememberSaveable)
    private val _toBeWatched = mutableStateListOf<Movie>()
    val toBeWatched: List<Movie> get() = _toBeWatched

    private val _watched = mutableStateListOf<Movie>()
    val watched: List<Movie> get() = _watched

    /** Add to 'to be watched' */
    fun addToBeWatched(movie: Movie) {
        // prevent duplicates by id
        if (_toBeWatched.none { it.id == movie.id } && _watched.none { it.id == movie.id }) {
            _toBeWatched.add(movie)
        }
    }

    /** Add directly to 'watched' */
    fun addWatched(movie: Movie) {
        if (_watched.none { it.id == movie.id }) {
            _watched.add(movie)
            // if it was in to-be-watched, remove it
            _toBeWatched.removeAll { it.id == movie.id }
        }
    }

    /** Mark a to-be-watched item as watched */
    fun markAsWatched(movie: Movie) {
        // remove from toBeWatched, then add to watched
        _toBeWatched.removeAll { it.id == movie.id }
        if (_watched.none { it.id == movie.id }) {
            _watched.add(movie)
        }
    }

    /** Remove from to-be-watched */
    fun removeFromToBeWatched(movie: Movie) {
        _toBeWatched.removeAll { it.id == movie.id }
    }

    /** Remove from watched */
    fun removeFromWatched(movie: Movie) {
        _watched.removeAll { it.id == movie.id }
    }

    /** Optional helper to clear all (for debug) */
    fun clearAll() {
        _toBeWatched.clear()
        _watched.clear()
    }
}
