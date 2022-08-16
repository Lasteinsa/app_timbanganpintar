package com.banksampahteratai.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.banksampahteratai.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        playAnimation()
        setupAction()
    }

    private fun playAnimation() {
        val imageLogin =
            ObjectAnimator.ofFloat(binding.imgLogo, "alpha", 1f).setDuration(500)
        val textWelcome =
            ObjectAnimator.ofFloat(binding.tvLogin, "alpha", 1f).setDuration(500)
        val usernameInput =
            ObjectAnimator.ofFloat(binding.inpUsername, "alpha", 1f).setDuration(300)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.inpPassword, "alpha", 1f).setDuration(300)
        val loginButton =
            ObjectAnimator.ofFloat(binding.btnLogin, "alpha", 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                imageLogin, textWelcome, usernameInput, passwordInput, loginButton
            )
            start()
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            tryLogin()
        }
    }

    private fun tryLogin() {
        binding.inpUsername
    }
}