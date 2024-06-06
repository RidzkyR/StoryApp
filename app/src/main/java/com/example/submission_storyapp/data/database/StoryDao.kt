package com.example.submission_storyapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submission_storyapp.data.api.responses.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: List<ListStoryItem>)

    @Query("SELECT * FROM tb_stories")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM tb_stories")
    suspend fun deleteAll()
}

