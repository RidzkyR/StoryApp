package com.example.submission_storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.submission_storyapp.data.Result
import com.example.submission_storyapp.data.UserRepository
import com.example.submission_storyapp.data.api.responses.StoryResponse
import com.example.submission_storyapp.data.preference.UserModel

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun isLogin(): LiveData<UserModel> = userRepository.isLogin().asLiveData()
    fun getListStoryItem(): LiveData<Result<StoryResponse>> = userRepository.getStories()

}