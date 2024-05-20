package com.example.submission_storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submission_storyapp.data.Result
import com.example.submission_storyapp.data.UserRepository
import com.example.submission_storyapp.data.api.responses.StoryResponse
import com.example.submission_storyapp.data.preference.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()
    fun getListStoryItem(): LiveData<Result<StoryResponse>> = userRepository.getStories()
    fun logOut() = viewModelScope.launch{userRepository.logout()}
}