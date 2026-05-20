package com.example.todoist.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoist.data.local.room.dao.TaskDao
import com.example.todoist.data.local.room.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
