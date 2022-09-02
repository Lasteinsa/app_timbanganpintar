package com.banksampahteratai.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.banksampahteratai.R
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseLogin
import com.banksampahteratai.databinding.ActivityLoginBinding
import com.banksampahteratai.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        loginViewModel = LoginViewModel(DataPreference(this))
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

    private fun isLoading(load: Boolean) {
        if(load) {
            binding.loadingLogin.root.visibility = View.VISIBLE
            binding.loadingLogin.root.bringToFront()
        } else {
            binding.loadingLogin.root.visibility = View.VISIBLE
        }
    }

    private fun alertInfo(isShow: Boolean) {
        val loginAlertShow =
            ObjectAnimator.ofFloat(binding.loginInfoAlert, "alpha", 1f).setDuration(500)
        val loginAlertHide =
            ObjectAnimator.ofFloat(binding.loginInfoAlert, "alpha", 0f).setDuration(500)

        if(isShow) {
            loginAlertShow.start()
        } else {
            loginAlertHide.start()
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            tryLogin()
        }
    }

    private fun tryLogin() {
        val username    = binding.inputUsername.text.toString()
        val password    = binding.inputPassword.text.toString()

        when {
            username.isEmpty()  -> {
                binding.inpUsername.error   = getString(R.string.username_cannot_empty)
            }
            password.isEmpty()  -> {
                binding.inpPassword.error   = getString(R.string.password_cannot_empty)
            }
            else -> {
                isLoading(true)
                this.currentFocus?.let {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(it.windowToken, 0)
                }
                binding.inpUsername.error   = null
                binding.inpPassword.error   = null

                val usernameMultipart = username.toRequestBody("text/plain".toMediaType())
                val passwordMultipart = password.toRequestBody("text/plain".toMediaType())
                val retrofitInstance    = ApiConfig.getApiService().login(usernameMultipart, passwordMultipart)
                retrofitInstance.enqueue(object : Callback<ResponseLogin> {
                    override fun onResponse(
                        call: Call<ResponseLogin>,
                        response: Response<ResponseLogin>
                    ) {
                        isLoading(false)
                        val responseBody    = response.body()
                        if(responseBody?.error == false) {
                            loginViewModel.login(responseBody)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java).apply{
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        } else {
                            alertInfo(true)
                            if(response.code() == 404) {
                                binding.loginInfoAlert.setBackgroundResource(R.drawable.alert_warning)
                                binding.alertInfo.text = getString(R.string.wrong_username_or_password)
                            } else {
                                binding.loginInfoAlert.setBackgroundResource(R.drawable.alert_danger)
                                binding.alertInfo.text = getString(R.string.server_fault)
                            }
                            binding.alertInfo.setTextColor(Color.WHITE)
                            val handler = Handler(Looper.getMainLooper())
                            handler.postDelayed({
                                alertInfo(false)
                            }, 4000)
                        }
                    }
                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        isLoading(false)
                        Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }
}