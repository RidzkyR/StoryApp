package com.example.submission_storyapp.view.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.submission_storyapp.R
import com.example.submission_storyapp.databinding.ActivityOnboardingBinding
import com.example.submission_storyapp.view.login.LoginActivity
import com.example.submission_storyapp.view.register.RegisterActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        with(binding){
            buttonBoardingLogin.setOnClickListener {
                val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@OnboardingActivity).toBundle())
            }
            buttonBoardingSignup.setOnClickListener {
                val intent = Intent(this@OnboardingActivity, RegisterActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@OnboardingActivity).toBundle())
            }
        }

    }

    private fun playAnimation() {
        with(binding) {
            val planes = arrayOf<View>(plane1, plane2, plane3, plane4)
            for (plane in planes) {
                val animator = ObjectAnimator.ofFloat(plane, View.TRANSLATION_X, -30f, 30f)
                animator.setDuration(6000)
                animator.repeatCount = ObjectAnimator.INFINITE
                animator.repeatMode = ObjectAnimator.REVERSE
                animator.start()
            }

            val icon = ObjectAnimator.ofFloat(ivOnboardingIcon, View.ALPHA, 1f).setDuration(300)
            val cloudLeft = ObjectAnimator.ofFloat(icCloudTopLeft, View.ALPHA, 1f).setDuration(300)
            val cloudRight = ObjectAnimator.ofFloat(ivCloudTopRight, View.ALPHA, 1f).setDuration(300)
            val cloudBottom = ObjectAnimator.ofFloat(ivCloudBottom, View.ALPHA, 1f).setDuration(300)
            val text = ObjectAnimator.ofFloat(tvText, View.ALPHA, 1f).setDuration(500)
            val btnLogin = ObjectAnimator.ofFloat(buttonBoardingLogin, View.ALPHA, 1f).setDuration(500)
            val btnSignup = ObjectAnimator.ofFloat(buttonBoardingSignup, View.ALPHA, 1f).setDuration(500)

            val together = AnimatorSet().apply { playTogether(text, btnLogin, btnSignup) }

            AnimatorSet().apply {
                playSequentially(icon, cloudRight, cloudLeft, cloudBottom, together)
                start()
            }
        }
    }
}