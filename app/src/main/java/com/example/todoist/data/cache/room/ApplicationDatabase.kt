package com.example.todoist.data.cache.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoist.data.cache.room.dao.TaskDao
import com.example.todoist.data.cache.room.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
