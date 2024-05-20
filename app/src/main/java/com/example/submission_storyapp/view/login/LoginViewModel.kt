package com.example.submission_storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission_storyapp.data.UserRepository
import com.example.submission_storyapp.data.preference.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = userRepository.login(email, password)
    fun saveSession(user: UserModel) = viewModelScope.launch { userRepository.saveSession(user) }
}