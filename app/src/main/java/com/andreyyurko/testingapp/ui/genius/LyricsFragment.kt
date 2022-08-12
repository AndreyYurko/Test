package com.andreyyurko.testingapp.ui.genius

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.andreyyurko.testingapp.R
import com.andreyyurko.testingapp.databinding.FragmentLyricsBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LyricsFragment : Fragment(R.layout.fragment_lyrics) {

    private val viewBinding by viewBinding(FragmentLyricsBinding::bind)

    private lateinit var lyrics : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("LyricsOfSong") { _, bundle ->
            lyrics = bundle.get("LyricsOfSongBundleKey").toString()
            viewBinding.lyricsTextView.text = lyrics
        }

        viewBinding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}