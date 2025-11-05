package com.yehali.todoapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yehali.todoapp.domain.model.Task
import java.util.Date

@Entity (tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long,                // Store Date as Long (timestamp)
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

//using mapper to convert the database entity to domain model
fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    dueDate = Date(dueDate),  //convert long back to date
    isCompleted = isCompleted
)

//using mapper to convert the domain model to database entity
fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    dueDate = dueDate.time, //convert date to long
    isCompleted = isCompleted

)