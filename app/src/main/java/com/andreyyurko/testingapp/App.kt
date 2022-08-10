package com.andreyyurko.testingapp


import android.app.Application
import androidx.viewbinding.BuildConfig
import com.andreyyurko.testingapp.core.MagicHandler
import dagger.hilt.android.HiltAndroidApp
import link.magic.android.Magic
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    val magic = Magic( "pk_live_53D1AFBBDE85B13A")

    override fun onCreate() {
        super.onCreate()
        initLogger()
    }

    private fun initLogger() {
        Timber.plant(Timber.DebugTree())
    }

}