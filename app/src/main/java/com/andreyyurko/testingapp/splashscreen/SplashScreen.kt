package com.andreyyurko.testingapp.splashscreen

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.andreyyurko.testingapp.databinding.ActivitySplashScreenBinding

import by.kirich1409.viewbindingdelegate.viewBinding
import com.andreyyurko.testingapp.AppMainActivity
import com.andreyyurko.testingapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreen : AppCompatActivity(R.layout.activity_splash_screen) {

    private val viewBinding by viewBinding(ActivitySplashScreenBinding::bind)

    val endSplashMills: Long = 4000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        fun ProgressBar.smoothProgress(percent: Int){
            val animation = ObjectAnimator.ofInt(this, "progress", percent)
            animation.duration = endSplashMills
            animation.interpolator = DecelerateInterpolator()
            animation.start()
        }

        viewBinding.progressBar.smoothProgress(100)

        Handler().postDelayed({
            val intent = Intent(this, AppMainActivity::class.java)
            startActivity(intent)
            finish()
        }, endSplashMills)
    }
}