package com.example.submission_storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.example.submission_storyapp.data.Result
import com.example.submission_storyapp.data.UserRepository
import com.example.submission_storyapp.data.api.responses.ListStoryItem
import com.example.submission_storyapp.data.api.responses.StoryResponse
import com.example.submission_storyapp.data.preference.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()
    fun getListStoryItem(): LiveData<PagingData<ListStoryItem>> = userRepository.getStories().cachedIn(viewModelScope)
    fun logOut() = viewModelScope.launch{userRepository.logout()}
}