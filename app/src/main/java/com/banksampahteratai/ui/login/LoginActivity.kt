package com.banksampahteratai.ui.login

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.banksampahteratai.R
import com.banksampahteratai.data.DataPreference
import com.banksampahteratai.data.Utility
import com.banksampahteratai.data.api.ApiConfig
import com.banksampahteratai.data.api.ResponseLogin
import com.banksampahteratai.databinding.ActivityLoginBinding
import com.banksampahteratai.ui.main.MainActivity
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var utility: Utility
    private lateinit var bounceAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        Glide.with(this).load(AppCompatResources.getDrawable(this,R.drawable.bgmain)).centerCrop().into(binding.bgMain)
        loginViewModel = LoginViewModel(DataPreference(this))
        utility = Utility()
        playAnimation()
        setupAction()
        setupAnimation()
        setupPermission()
    }

    private fun setupPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                utility.showDialog(
                    this@LoginActivity,
                    "Izinkan Akses",
                    "Mohon Izinkan Akses Untuk Fitur Bluetooth",
                    "OK",
                    "",
                    false,
                    fun() {
                            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT),2022)
                    }
                )
            }
        }
    }

    private fun setupAnimation() {
        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
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
            it.startAnimation(bounceAnim)
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
                utility.showLoading(this, false)
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
                        utility.hideLoading()
                        val responseBody    = response.body()
                        if(responseBody?.error == false) {
                            loginViewModel.login(responseBody)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java).apply{
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        } else {
                            if(response.code() == 404) {
                                utility.showSnackbar(this@LoginActivity, binding.root, getString(R.string.wrong_username_or_password), false)
                            } else {
                                utility.showSnackbar(this@LoginActivity, binding.root, getString(R.string.server_fault), true)
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        utility.hideLoading()
                        utility.showSnackbar(this@LoginActivity, binding.root, "Tidak Ada Internet!", true)
                    }
                })
            }
        }

    }
}