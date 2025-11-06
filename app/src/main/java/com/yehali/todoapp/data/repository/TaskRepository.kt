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
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }  // Convert each entity to domain model
        }
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    fun getCompletedTasksCount(): Flow<Int> {
        return taskDao.getCompletedTasksCount()
    }

    fun getTotalTasksCount(): Flow<Int> {
        return taskDao.getTotalTasksCount()
    }
}
