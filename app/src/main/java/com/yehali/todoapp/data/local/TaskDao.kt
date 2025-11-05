package com.yehali.todoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT *FROM tasks ORDER BY isCompleted ASC, dueDate ASC") //sorting by due date
    fun getAllTasks(): Flow<List<TaskEntity>>  //Flow - Reactive stream that emits updates when data changes

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById (taskId: Int): TaskEntity?  //coroutine

    //inserting a new task
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask (task: TaskEntity): Long

    //updating a task
    @Update
    suspend fun updateTask(task: TaskEntity)

    //deleting a task
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    //count completed tasks
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasksCount(): Flow<Int>

    //count total tasks
    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTasksCount(): Flow<Int>





}