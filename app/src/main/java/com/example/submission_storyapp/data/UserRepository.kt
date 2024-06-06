package com.example.submission_storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.submission_storyapp.data.api.responses.AddStoryResponse
import com.example.submission_storyapp.data.api.responses.ErrorResponse
import com.example.submission_storyapp.data.api.responses.ListStoryItem
import com.example.submission_storyapp.data.api.responses.LoginErrorResponse
import com.example.submission_storyapp.data.api.retrofit.ApiService
import com.example.submission_storyapp.data.api.responses.LoginResponse
import com.example.submission_storyapp.data.api.responses.RegisterResponse
import com.example.submission_storyapp.data.api.responses.StoryResponse
import com.example.submission_storyapp.data.api.retrofit.ApiConfig
import com.example.submission_storyapp.data.database.StoryDatabase
import com.example.submission_storyapp.data.database.StoryRemoteMediator
import com.example.submission_storyapp.data.preference.UserModel
import com.example.submission_storyapp.data.preference.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private var apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
) {
    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.register(name, email, password)
            emit(Result.Success(result))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            e.printStackTrace()
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
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
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStories(coroutineScope: CoroutineScope): LiveData<Result<PagingData<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = runBlocking {
                userPreference.getSession().first().token
            }
            apiService = ApiConfig.getApiService(token)
            @OptIn(ExperimentalPagingApi::class)
            val result = Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
                pagingSourceFactory = { storyDatabase.storyDao().getAllStories()}
            )
            val couroutineFlow = result.flow.cachedIn(coroutineScope)
            couroutineFlow.collect { pagingData ->
                emit(Result.Success(pagingData))
            }
        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(response, StoryResponse::class.java)
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addStory(imageFile: File, description: String, lat: Float? = null , lon: Float? = null) = liveData {
        emit(Result.Loading)
        val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadStory(multipartBody, requestBody, lat, lon)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(response, AddStoryResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = runBlocking {
                userPreference.getSession().first().token
            }
            apiService = ApiConfig.getApiService(token)
            val result = apiService.getStoriesWithLocation()
            emit(Result.Success(result))
        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(response, StoryResponse::class.java)
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) = userPreference.saveSession(user)

    suspend fun logout() = userPreference.logOut()

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference, storyDatabase)
            }.also { instance = it }
    }
}