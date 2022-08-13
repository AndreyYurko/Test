package com.andreyyurko.testingapp.ui.genius.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreyyurko.testingapp.core.NetworkHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val networkHandler: NetworkHandler
) : ViewModel() {
    companion object {
        val LOG_TAG = "GeniusViewModel"
    }

    sealed class LoadSongsActionState {
        object Loading : LoadSongsActionState()
        data class Data(val lyrics: String) : LoadSongsActionState()
    }

    private val _loadSongsActionState = MutableStateFlow<LoadSongsActionState>(
        LoadSongsActionState.Loading
    )

    val loadSongsActionState : Flow<LoadSongsActionState> get() = _loadSongsActionState.asStateFlow()

    fun getLyrics(authorName: String, songName: String) {
        viewModelScope.launch {
            _loadSongsActionState.emit(LoadSongsActionState.Loading)
            lateinit var lyrics: String

            networkHandler.getLyrics(authorName, songName).collect() {
                lyrics = it
                _loadSongsActionState.emit(LoadSongsActionState.Data(it))
            }

        }
    }
}