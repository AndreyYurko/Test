package com.andreyyurko.testingapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.andreyyurko.testingapp.core.MagicHandler
import com.andreyyurko.testingapp.core.NetworkHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppMainActivity : AppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var magicHandler: MagicHandler

    @Inject
    lateinit var networkHandler: NetworkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMagic()
        magicHandler.startRelayer(this)
        subscribeToAuthorizationStatus()
    }

    private fun initMagic() {
        val magic = (applicationContext as App).magic
        magicHandler.init(magic)
    }

    private fun subscribeToAuthorizationStatus() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                magicHandler.authStateFlow().collect { response ->
                    when (response) {
                        MagicHandler.AuthState.Logged -> {
                            showSuitableNavigationFlow(true)
                        }
                        else -> {
                            showSuitableNavigationFlow(false)
                        }
                    }

                }
            }
        }
    }

    private fun showSuitableNavigationFlow(isAuthorized: Boolean) {
        val navController = findNavController(R.id.mainActivityNavigationHost)
        when (isAuthorized) {
            true -> {
                if (navController.backQueue.any { it.destination.id == R.id.registered_user_nav_graph}) {
                    return
                }
                navController.navigate(R.id.action_RegisteredUserNavGraph)
            }
            false -> {
                if (navController.backQueue.any { it.destination.id == R.id.guest_nav_graph}) {
                    return
                }
                navController.navigate(R.id.action_guestNavGraph)
            }
        }
    }

    override fun onDestroy() {
        magicHandler.destroyRelayer()
        super.onDestroy()
    }
}