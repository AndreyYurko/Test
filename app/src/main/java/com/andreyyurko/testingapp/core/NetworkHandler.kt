package com.andreyyurko.testingapp.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await
import java.net.URLEncoder.encode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHandler @Inject constructor()
    : ViewModel() {

    private val client = OkHttpClient()

    private fun search(item: String) {
        val url = "https://genius.p.rapidapi.com/search?q=" + encode(item, "utf-8")
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-RapidAPI-Key", "6d0c8dc11dmsh3c73bbef55d1600p1204a5jsn4c65a6eadf7f")
            .addHeader("X-RapidAPI-Host", "genius.p.rapidapi.com")
            .build()

        viewModelScope.launch {
            val response = client.newCall(request).await()
        }
    }
}