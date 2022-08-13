package com.andreyyurko.testingapp.ui.genius.songslist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import by.kirich1409.viewbindingdelegate.viewBinding
import com.andreyyurko.testingapp.R
import com.andreyyurko.testingapp.data.Song
import com.andreyyurko.testingapp.databinding.FragmentGeniusBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeniusFragment : Fragment(R.layout.fragment_genius) {

    companion object {
        val LOG_TAG = "GeniusFragment"
    }

    private val viewBinding by viewBinding(FragmentGeniusBinding::bind)

    private var songsList = emptyList<Song>()

    private lateinit var viewModel: GeniusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[GeniusViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewBinding.sendButton.setOnClickListener {
            val text = viewBinding.queryEditText.text.toString()
            viewModel.search(text)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadSongsActionState.collect { viewState ->
                    renderViewState(viewState)
                }
            }
        }
    }

    private fun renderViewState(viewState: GeniusViewModel.LoadSongsActionState) {
        when (viewState) {
            is GeniusViewModel.LoadSongsActionState.Loading -> {
                viewBinding.songsRecyclerView.isVisible = false
                viewBinding.progressBar.isVisible = true
            }
            is GeniusViewModel.LoadSongsActionState.Data -> {
                viewBinding.songsRecyclerView.isVisible = true
                songsList = viewState.songsList
                (viewBinding.songsRecyclerView.adapter as SongListAdapter).apply {
                    songsList = viewState.songsList
                    notifyDataSetChanged()
                }
                viewBinding.progressBar.isVisible = false
            }
        }
    }

    private fun setupRecyclerView(): SongListAdapter {
        val recyclerView = viewBinding.songsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = SongListAdapter()
        recyclerView.adapter = adapter
        return adapter
    }

}