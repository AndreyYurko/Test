package com.andreyyurko.testingapp.core

import android.util.Log
import androidx.lifecycle.ViewModel
import com.andreyyurko.testingapp.data.Song
import kotlinx.coroutines.flow.flow
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import org.jsoup.Jsoup
import ru.gildor.coroutines.okhttp.await
import java.io.IOException
import java.lang.NullPointerException
import java.net.URLEncoder.encode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHandler @Inject constructor()
    : ViewModel() {

    companion object {
        val LOG_TAG = "NetworkHandler"
    }

    private val host1 = "genius.p.rapidapi.com"
    private val host2 = "theaudiodb.p.rapidapi.com"

    private val client = OkHttpClient()

    fun search(item: String) = flow {

        val url = "https://genius.p.rapidapi.com/search?q=" + encode(item, "utf-8")
        try {
            val response = getRespond(url, host1)
            val responseBody = response.body?.string() ?: ""
            //Log.d(LOG_TAG, responseBody)
            if (responseBody.substring(0, 5) != "error") {
                val jsonObject = JSONTokener(responseBody).nextValue() as JSONObject
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
            else {
                emit(mutableListOf())
            }
        }
        catch (e: IOException) {
            emit(mutableListOf())
        }
    }

    fun getLyrics(authorName: String, songName: String) = flow {
        // isn't the best solution
        val url = "https://azlyrics.com/lyrics/" +
                "${encode(authorName
                    .replace(" ", "")
                    .lowercase()
                    .replace("the", ""), "utf-8")}/" +
                "${encode(songName
                    .replace(" ", "")
                    .lowercase(), "utf-8")}.html"

        Log.d(LOG_TAG, url)

        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            val response = client.newCall(request).await()
            val responseBody = response.body?.string().toString()
            try {
                val lyrics = getLyrics(responseBody)
                emit(lyrics)
            }
            catch (e: NullPointerException) {
                val lyrics = "Sorry, can't find lyrics :("
                emit(lyrics)
            }
        }
        catch (e: IOException) {
            emit("Sorry, can't find lyrics :(")
        }
    }

    private suspend fun getRespond(url: String, host: String): Response {
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-RapidAPI-Key", "6d0c8dc11dmsh3c73bbef55d1600p1204a5jsn4c65a6eadf7f")
            .addHeader("X-RapidAPI-Host", host)
            .build()

        return client.newCall(request).await()
    }

    private fun getLyrics(body: String): String {
        val regexStart = """<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->""".toRegex()
        val matchStart = regexStart.find(body)
        val regexEnd = """<!-- MxM banner -->""".toRegex()
        val matchEnd = regexEnd.find(body)
        Log.d(LOG_TAG, body)
        val lyrics = body.substring(matchStart!!.range.last + 2, matchEnd!!.range.last - 2)
            .replace("<br>", "")
            .replace("</div>", "")
        return lyrics
    }

}