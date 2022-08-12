package com.andreyyurko.testingapp.ui.genius

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreyyurko.testingapp.core.NetworkHandler
import com.andreyyurko.testingapp.data.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeniusViewModel @Inject constructor(
    private val networkHandler: NetworkHandler
) : ViewModel() {

    companion object {
        val LOG_TAG = "GeniusViewModel"
    }

    sealed class LoadSongsActionState {
        object Loading : LoadSongsActionState()
        data class Data(val songsList: List<Song>) : LoadSongsActionState()
    }

    private val _loadSongsActionState = MutableStateFlow<LoadSongsActionState>(
        LoadSongsActionState.Data(
            emptyList()
        )
    )

    val loadSongsActionState : Flow<LoadSongsActionState> get() = _loadSongsActionState.asStateFlow()

    fun search(item: String) {
        viewModelScope.launch {
            _loadSongsActionState.emit(LoadSongsActionState.Loading)
            lateinit var songs: MutableList<Song>

            networkHandler.search(item).collect() {
                songs = it
                _loadSongsActionState.emit(LoadSongsActionState.Data(songs))
            }

        }
    }

}