package com.yehali.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TaskEntity::class], //list of all the entities
    version = 1,
    exportSchema = false
)
abstract class TaskDB : RoomDatabase() {
    abstract fun taskDao(): TaskDao //giving access to the DAO

    companion object {
        const val DATABASE_NAME = "task_database"
    }
}