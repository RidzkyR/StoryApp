package com.example.submission_storyapp.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.submission_storyapp.data.UserRepository
import com.example.submission_storyapp.data.preference.UserModel
import java.io.File

class AddViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getUser(): LiveData<UserModel> = userRepository.getSession().asLiveData()
    fun addStory(file: File, description: String) = userRepository.addStory(file, description)
}