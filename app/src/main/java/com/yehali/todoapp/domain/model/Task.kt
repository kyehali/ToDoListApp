package com.yehali.todoapp.domain.model

import java.util.Date

//domain model representing a task
//this is the cpre business obj that app works with
data class Task (
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Date,
    val isCompleted: Boolean = false
) {}