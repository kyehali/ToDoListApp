package com.yehali.todoapp.data.repository

import com.yehali.todoapp.data.local.TaskDao
import com.yehali.todoapp.data.local.toDomain
import com.yehali.todoapp.data.local.toEntity
import com.yehali.todoapp.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    /**
     * Get all tasks as domain models
     * Flow automatically updates when database changes
     */
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }  // Convert each entity to domain model
        }
    }

    /**
     * Get a task by ID
     */
    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    /**
     * Insert a new task
     * Returns the ID of the created task
     */
    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    /**
     * Update an existing task
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    /**
     * Delete a task
     */
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    /**
     * Get count of completed tasks
     */
    fun getCompletedTasksCount(): Flow<Int> {
        return taskDao.getCompletedTasksCount()
    }

    /**
     * Get total task count
     */
    fun getTotalTasksCount(): Flow<Int> {
        return taskDao.getTotalTasksCount()
    }
}
