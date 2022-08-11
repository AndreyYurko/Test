package com.andreyyurko.testingapp.ui.genius

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andreyyurko.testingapp.R
import com.andreyyurko.testingapp.data.Song

class SongListAdapter : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {

    var songsList: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songNameTextView.text = songsList[position].Name
        holder.authorNameTextView.text = songsList[position].Author
        holder.lyricsTextView.text = songsList[position].Lyrics
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songNameTextView = itemView.findViewById<TextView>(R.id.songNameTextView)
        val authorNameTextView = itemView.findViewById<TextView>(R.id.authorNameTextView)
        val lyricsTextView = itemView.findViewById<TextView>(R.id.LyricsTextView)
    }

}