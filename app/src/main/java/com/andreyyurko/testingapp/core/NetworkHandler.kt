package com.andreyyurko.testingapp.core

import android.util.Log
import androidx.lifecycle.ViewModel
import com.andreyyurko.testingapp.data.Song
import kotlinx.coroutines.flow.flow
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import ru.gildor.coroutines.okhttp.await
import timber.log.Timber
import java.io.IOException
import java.net.URLEncoder.encode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHandler @Inject constructor()
    : ViewModel() {

    companion object {
        val LOG_TAG = "NetworkHandler"
    }

    private val client = OkHttpClient()

    fun search(item: String) = flow {

        val url = "https://genius.p.rapidapi.com/search?q=" + encode(item, "utf-8")
        val response = getRespond(url)
        val jsonObject = JSONTokener(response.body?.string() ?: "").nextValue() as JSONObject
        val jsonArrayHits = jsonObject.getJSONObject("response").getJSONArray("hits")
        val songsList: MutableList<Song> = mutableListOf()
        for (i in 0 until jsonArrayHits.length()) {
            Log.d(LOG_TAG, jsonArrayHits.toString())
            val jsonResult = jsonArrayHits.getJSONObject(i).getJSONObject("result")
            songsList.add(
                Song(
                    jsonResult.getInt("id"),
                    jsonResult.getString("title_with_featured"),
                    jsonResult.getJSONObject("primary_artist").getInt("id"),
                    jsonResult.getString("artist_names"),
                    "NoLyricsYet"
                )
            )
        }
        Log.d(LOG_TAG, songsList.size.toString())
        emit(songsList)
    }

    fun getArtistSongs(id: Int) = flow {
        val url = "https://genius.p.rapidapi.com/artists/$id/songs"
        val response = getRespond(url)
        emit(response)
    }

    fun getArtist(id : Int) = flow {
        val url = "https://genius.p.rapidapi.com/artists/$id"
        val response = getRespond(url)
        emit(response)
    }

    fun getSong(id : Int) = flow {
        val url = "https://genius.p.rapidapi.com/songs/$id"
        val response = getRespond(url)
        emit(response)
    }

    private suspend fun getRespond(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-RapidAPI-Key", "6d0c8dc11dmsh3c73bbef55d1600p1204a5jsn4c65a6eadf7f")
            .addHeader("X-RapidAPI-Host", "genius.p.rapidapi.com")
            .build()

        val response = client.newCall(request).await()
        return response
    }

    sealed class QueryActionState {
        object Waiting : QueryActionState()
        object GetResponse : QueryActionState()
    }

}