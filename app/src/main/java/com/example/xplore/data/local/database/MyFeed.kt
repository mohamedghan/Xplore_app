package com.example.xplore.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class MyFeed(
    @PrimaryKey val id: String,
    val username: String,
    val userProfileImage: Int,
    val postImage: Int,
    val likes: Int,
    val caption: String,
    val likedByMe: Boolean
)

@Dao
interface MyFeedDao {
    @Query("SELECT * FROM myfeed ORDER BY likes DESC LIMIT 10")
    fun getMyFeeds(): Flow<List<MyFeed>>

    @Insert
    suspend fun insertMyFeeds(item: MyFeed)
}
