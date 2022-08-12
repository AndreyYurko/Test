package com.andreyyurko.testingapp.ui.genius

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.andreyyurko.testingapp.R
import com.andreyyurko.testingapp.data.Song

class SongListAdapter : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {

    companion object {
        val LOG_TAG = "SongListAdapter"
    }

    var songsList: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songNameTextView.text = songsList[position].Name
        holder.authorNameTextView.text = songsList[position].Author
        holder.lyrics = songsList[position].Lyrics
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songNameTextView = itemView.findViewById<TextView>(R.id.songNameTextView)
        val authorNameTextView = itemView.findViewById<TextView>(R.id.authorNameTextView)
        val lyricsTextView = itemView.findViewById<TextView>(R.id.lyricsTextView)
        var lyrics : String = ""

        init {
            lyricsTextView?.setOnClickListener {
                Log.d(LOG_TAG, "Click!")
                it.findNavController().navigate(R.id.action_GeniusFragment_to_LyricsFragment)
                it.findFragment<GeniusFragment>().setFragmentResult(
                        "LyricsOfSong", bundleOf(
                        "LyricsOfSongBundleKey" to lyrics
                    )
                )
            }
        }
    }

}