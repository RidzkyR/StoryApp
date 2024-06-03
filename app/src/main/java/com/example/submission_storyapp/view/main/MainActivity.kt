package com.example.submission_storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission_storyapp.R
import com.example.submission_storyapp.data.Result
import com.example.submission_storyapp.data.api.responses.ListStoryItem
import com.example.submission_storyapp.databinding.ActivityMainBinding
import com.example.submission_storyapp.view.ViewModelFactory
import com.example.submission_storyapp.view.add.AddActivity
import com.example.submission_storyapp.view.onboarding.OnboardingActivity
import android.provider.Settings
import com.example.submission_storyapp.view.map.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        getSession()
        setupAction()
    }

    private fun setupAction() {
        with(binding) {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu1 -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }
                    R.id.menu2 -> {
                        logout()
                        true
                    }
                    R.id.menu3 ->{
                        startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                        true
                    }

                    else -> false
                }
            }

            fabUpload.setOnClickListener {
                intent = Intent(this@MainActivity, AddActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                setupListStoryItem()
            } else {
                intent = Intent(this@MainActivity, OnboardingActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
    }

    private fun logout() {
        viewModel.logOut()
        finishAffinity()
    }

    private fun setupListStoryItem() {
        val adapter = MainAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.getListStoryItem().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
