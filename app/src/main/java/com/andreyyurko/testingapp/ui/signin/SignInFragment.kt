package com.andreyyurko.testingapp.ui.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.andreyyurko.testingapp.R
import by.kirich1409.viewbindingdelegate.viewBinding
import com.andreyyurko.testingapp.core.MagicHandler
import com.andreyyurko.testingapp.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private val viewBinding by viewBinding(FragmentSignInBinding::bind)

    @Inject
    lateinit var magicHandler : MagicHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("SignIn")

        viewBinding.sendButton.setOnClickListener {
            val email = viewBinding.emailEditText.text ?: ""
            magicHandler.auth(email.toString())

        }
    }

}