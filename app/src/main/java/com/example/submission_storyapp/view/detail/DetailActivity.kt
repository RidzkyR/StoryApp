package com.example.submission_storyapp.view.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.submission_storyapp.R
import com.example.submission_storyapp.data.api.responses.ListStoryItem
import com.example.submission_storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //menangkap userID
        val user = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_ITEM, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_ITEM)
        }

        binding.tvDetailUserId.text = user?.id
        binding.tvDetailName.text = user?.name
        binding.tvDetailDate.text = user?.createdAt
        binding.tvDetailDescription.text = user?.description
        Glide
            .with(this@DetailActivity)
            .load(user?.photoUrl)
            .into(binding.ivDetailPhoto)
    }

    companion object{
        const val EXTRA_ITEM = "extra_item"
    }
}