package com.andreyyurko.testingapp.ui.genius.songslist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
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
        holder.authorName = songsList[position].Author
        holder.songName = songsList[position].Name


        //Log.d(LOG_TAG, holder.songNameTextView.currentTextColor.toString())
        //Log.d(LOG_TAG, R.color.white.toString())

        holder.lyricsTextView.setOnClickListener {
            Log.d(LOG_TAG, "Click!")
            it.transitionName = holder.authorName + holder.songName + "TextView"
            val extras = FragmentNavigatorExtras(
                holder.lyricsTextView to holder.authorName + holder.songName + "TextView"
            )
            it.findNavController().navigate(R.id.action_GeniusFragment_to_LyricsFragment, null, null, extras)
            it.findFragment<GeniusFragment>().setFragmentResult(
                "LyricsOfSong", bundleOf(
                    "AuthorOfSongBundleKey" to holder.authorName,
                    "SongOfSongBundleKey" to holder.songName
                )
            )

        }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songNameTextView = itemView.findViewById<TextView>(R.id.songNameTextView)
        val authorNameTextView = itemView.findViewById<TextView>(R.id.authorNameTextView)
        val lyricsTextView = itemView.findViewById<TextView>(R.id.lyricsTextView)
        var authorName : String = ""
        var songName : String = ""

        init {
            songNameTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.genius_text_colour))
            authorNameTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.genius_text_colour))
            lyricsTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.genius_text_colour))
        }
    }

}