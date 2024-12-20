package com.example.xplore.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MyFeed::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myFeedDao(): MyFeedDao
}
