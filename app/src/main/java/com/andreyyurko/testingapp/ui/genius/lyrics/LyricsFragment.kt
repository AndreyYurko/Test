package com.andreyyurko.testingapp.ui.genius.lyrics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.andreyyurko.testingapp.R
import com.andreyyurko.testingapp.databinding.FragmentLyricsBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LyricsFragment : Fragment(R.layout.fragment_lyrics) {

    private val viewBinding by viewBinding(FragmentLyricsBinding::bind)

    private lateinit var viewModel: LyricsViewModel

    private lateinit var authorName : String
    private lateinit var songName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LyricsViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("LyricsOfSong") { _, bundle ->
            authorName = bundle.get("AuthorOfSongBundleKey").toString()
            songName = bundle.get("SongOfSongBundleKey").toString()
            viewModel.getLyrics(authorName, songName)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadSongsActionState.collect() { viewState ->
                    renderViewState(viewState)
                }
            }
        }

        viewBinding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun renderViewState(viewState: LyricsViewModel.LoadSongsActionState) {
        when (viewState) {
            is LyricsViewModel.LoadSongsActionState.Loading -> {
                viewBinding.lyricsTextView.text = "Loading..."
            }
            is LyricsViewModel.LoadSongsActionState.Data -> {
                viewBinding.lyricsTextView.text = viewState.lyrics
            }
        }
    }

}