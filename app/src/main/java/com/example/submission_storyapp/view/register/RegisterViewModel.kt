package com.example.submission_storyapp.view.register

import androidx.lifecycle.ViewModel
import com.example.submission_storyapp.data.UserRepository

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = userRepository.register(name, email, password)
}