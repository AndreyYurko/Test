package com.andreyyurko.testingapp.core

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import link.magic.android.Magic
import link.magic.android.modules.auth.requestConfiguration.LoginWithMagicLinkConfiguration
import link.magic.android.modules.auth.response.DIDToken
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MagicHandler @Inject constructor()
    : ViewModel() {

    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.NotLogged)

    private lateinit var magic : Magic

    private var isInit = false

    fun init(_magic: Magic) {
        Timber.d("init magic")
        if (!isInit) {
            magic = _magic
            isInit = true
        }
    }

    fun startRelayer(context: Context) {
        Timber.d("StartRelayer")
        magic.startRelayer(context)
    }

    fun destroyRelayer() {
        Timber.d("EndRelayer")
        magic.destroyRelayer()
    }

    fun authStateFlow(): Flow<AuthState> {
        return _authStateFlow.asStateFlow()
    }

    fun auth(email: String) {
        Timber.d("send email")
        val result = magic.auth.loginWithMagicLink(LoginWithMagicLinkConfiguration(email))
        Timber.d("result")

        result.whenComplete { response: DIDToken?, error: Throwable? ->
            Timber.d("get response")
            if (error != null) {
                Timber.d("error", error.message)
                //Handle Error
            }

            if (response != null && !response.hasError()) {
                Timber.d("Magic", "You're logged in." + response.result)
                viewModelScope.launch {
                    _authStateFlow.emit(AuthState.Logged)
                }
            } else {
                Timber.d("login", "Not Logged in")
                viewModelScope.launch {
                    _authStateFlow.emit(AuthState.NotLogged)
                }
            }
        }

    }

    sealed class AuthState {
        object NotLogged : AuthState()
        object Logged : AuthState()
    }

}