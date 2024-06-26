package com.example.submission_storyapp.di

import android.content.Context
import com.example.submission_storyapp.data.UserRepository
import com.example.submission_storyapp.data.api.retrofit.ApiConfig
import com.example.submission_storyapp.data.database.StoryDatabase
import com.example.submission_storyapp.data.preference.UserPreference
import com.example.submission_storyapp.data.preference.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref, database)
    }
}