package com.example.submission_storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.submission_storyapp.data.api.responses.ErrorResponse
import com.example.submission_storyapp.data.api.responses.LoginErrorResponse
import com.example.submission_storyapp.data.api.retrofit.ApiService
import com.example.submission_storyapp.data.api.responses.LoginResponse
import com.example.submission_storyapp.data.api.responses.RegisterResponse
import com.example.submission_storyapp.data.api.responses.StoryResponse
import com.example.submission_storyapp.data.api.retrofit.ApiConfig
import com.example.submission_storyapp.data.preference.UserModel
import com.example.submission_storyapp.data.preference.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class UserRepository private constructor(private var apiService: ApiService, private val userPreference: UserPreference) {
    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.register(name, email, password)
            emit(Result.Success(result))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            e.printStackTrace()
            emit(Result.Error(errorBody.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.login(email, password)
            emit(Result.Success(result))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginErrorResponse::class.java)
            e.printStackTrace()
            emit(Result.Error(errorBody.message.toString()))
        }
    }

    fun getStories(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = runBlocking {
                userPreference.getUser().first().token
            }
            apiService = ApiConfig.getApiService(token)
            val result = apiService.getStories()
            emit(Result.Success(result))
        }catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(response, StoryResponse::class.java)
            emit(Result.Error(error.message.toString()))
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailStory(userId: String): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = runBlocking {
                userPreference.getUser().first().token
            }
            apiService = ApiConfig.getApiService(token)
            val result = apiService.getDetailStory()
            emit(Result.Success(result))
        }catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(response, StoryResponse::class.java)
            emit(Result.Error(error.message.toString()))
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }
    suspend fun saveUser(user: UserModel) = userPreference.saveUser(user)

    suspend fun logout() = userPreference.logOut()

    fun isLogin(): Flow<UserModel> = userPreference.getUser()

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}