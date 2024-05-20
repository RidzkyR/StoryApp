package com.example.submission_storyapp.view.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.submission_storyapp.R
import com.example.submission_storyapp.databinding.ActivityOnboardingBinding
import com.example.submission_storyapp.view.login.LoginActivity

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

        playAnimation()

        binding.fabOnboarding.setOnClickListener {
            val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        val planes = arrayOf<View>(binding.plane1, binding.plane2, binding.plane3, binding.plane4)

        for (plane in planes) {
            val animator = ObjectAnimator.ofFloat(plane, View.TRANSLATION_X, -30f, 30f)
            animator.setDuration(6000)
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.start()
        }


        val icon = ObjectAnimator.ofFloat(binding.ivOnboardingIcon, View.ALPHA, 1f).setDuration(300)
        val cloudLeft = ObjectAnimator.ofFloat(binding.icCloudTopLeft, View.ALPHA, 1f).setDuration(300)
        val cloudRight = ObjectAnimator.ofFloat(binding.ivCloudTopRight, View.ALPHA, 1f).setDuration(300)
        val cloudBottom = ObjectAnimator.ofFloat(binding.ivCloudBottom, View.ALPHA, 1f).setDuration(300)
        val text = ObjectAnimator.ofFloat(binding.tvFabText, View.ALPHA, 1f).setDuration(500)
        val fab = ObjectAnimator.ofFloat(binding.fabOnboarding, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(text, fab)
        }

        AnimatorSet().apply {
            playSequentially(icon,cloudRight,cloudLeft,cloudBottom,together)
            start()
        }
    }
}