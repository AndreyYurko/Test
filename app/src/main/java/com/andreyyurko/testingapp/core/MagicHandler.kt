package com.andreyyurko.testingapp.core

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mojoauth.android.api.MojoAuthApi
import com.mojoauth.android.handler.AsyncHandler
import com.mojoauth.android.helper.ErrorResponse
import com.mojoauth.android.helper.MojoAuthSDK.Initialize
import com.mojoauth.android.models.responsemodels.LoginResponse
import com.mojoauth.android.models.responsemodels.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MagicHandler @Inject constructor()
    : ViewModel() {

    companion object {
        private val LOG_TAG = "Magic handler"
    }

    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.NotLogged)

    fun authStateFlow(): Flow<AuthState> {
        return _authStateFlow.asStateFlow()
    }

    private var isInit = false
    private var accessToken = ""
    private var stateId = ""

    private val timer = timer()

    fun init() {
        Timber.d("init magic")
        if (!isInit) {
            val initialize = Initialize()
            Initialize.setApiKey("test-560d3c9f-e98f-4b45-b757-f70de707db37")
            checkStatus()
            isInit = true
        }
    }

    fun loginByMagicLink(email: String) {
        val mojoAuthApi = MojoAuthApi()
        mojoAuthApi.loginByMagicLink(
            email,
            "<language>",
            "<redirect-URL>",
            object : AsyncHandler<LoginResponse> {
                override fun onFailure(error: ErrorResponse) {
                    Log.d("Error", error.description)
                }

                override fun onSuccess(data: LoginResponse) {
                    Log.d("Data", data.stateId)
                    stateId = data.stateId
                }
            })
    }

    fun checkStatus() {
        timer.start()
    }

    fun loginByEmailOTP() {
        val mojoAuthApi = MojoAuthApi()
        mojoAuthApi.loginByEmailOTP("<email>", "<language>", object : AsyncHandler<LoginResponse> {
            override fun onFailure(error: ErrorResponse) {
                Log.d("Error", error.description)
            }

            override fun onSuccess(data: LoginResponse) {
                Log.d("Data", data.stateId)
            }
        })
    }

    fun verifyEmailOTP() {
        val mojoAuthApi = MojoAuthApi()
        mojoAuthApi.verifyEmailOTP("<OTP>", "<stateId>", object : AsyncHandler<UserResponse> {
            override fun onFailure(error: ErrorResponse) {
                Log.d("Error", error.description)
            }

            override fun onSuccess(data: UserResponse) {
                Log.d("IsAuthenticated", data.authenticated.toString())
            }
        })
    }

    private fun timer() : CountDownTimer {
        return object: CountDownTimer(10000, 10000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                val mojoAuthApi = MojoAuthApi()
                Log.d(LOG_TAG, "check")
                mojoAuthApi.pingStatus(stateId, object : AsyncHandler<UserResponse> {
                    override fun onFailure(error: ErrorResponse) {
                        Log.d("Error", error.description)
                    }

                    override fun onSuccess(data: UserResponse) {
                        Log.d("IsAuthenticated", data.authenticated.toString())
                        //Log.d("access_token", data.oauth.accessToken)
                        if (data.authenticated) {
                            accessToken = data.oauth.accessToken
                            viewModelScope.launch {
                                _authStateFlow.emit(AuthState.Logged)
                            }
                        }
                    }
                })
                timer.cancel()
                timer.start()
            }
        }
    }


    sealed class AuthState {
        object NotLogged : AuthState()
        object Logged : AuthState()
    }

}