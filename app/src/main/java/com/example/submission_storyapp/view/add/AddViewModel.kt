package com.example.submission_storyapp.view.add

import androidx.lifecycle.ViewModel
import com.example.submission_storyapp.data.UserRepository
import java.io.File

class AddViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun addStory(file: File, description: String) = userRepository.addStory(file, description)
}