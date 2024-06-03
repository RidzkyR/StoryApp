package com.example.submission_storyapp.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.submission_storyapp.data.Result
import com.example.submission_storyapp.data.UserRepository
import com.example.submission_storyapp.data.api.responses.StoryResponse

class MapsViewModel(private val userRepository: UserRepository): ViewModel() {
    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = userRepository.getStoriesWithLocation()
}