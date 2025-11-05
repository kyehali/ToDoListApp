package com.yehali.todoapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yehali.todoapp.data.repository.TaskRepository
import com.yehali.todoapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class HomeState(
    val todayTasks: List<Task> = emptyList(),
    val tomorrowTasks: List<Task> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedPercentage: Int = 0,
    val isLoading: Boolean = true,
    val filterType: FilterType = FilterType.ALL,
    val filterDate: Date? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    private val _filterType = MutableStateFlow(FilterType.ALL)
    private val _filterDate = MutableStateFlow<Date?>(null)
    val state: StateFlow<HomeState> = combine(
        repository.getAllTasks(),
        repository.getCompletedTasksCount(),
        repository.getTotalTasksCount(),
        _filterType,
        _filterDate
    ) { tasks, completedCount, totalCount, filterType, filterDate ->
        // Apply filters
        val filteredTasks = tasks.filter { task ->
            val matchesCompletionFilter = when (filterType) {
                FilterType.ALL -> true
                FilterType.COMPLETED -> task.isCompleted
                FilterType.UNCOMPLETED -> !task.isCompleted
            }

            val matchesDateFilter = if (filterDate != null) {
                getStartOfDay(task.dueDate).time == getStartOfDay(filterDate).time
            } else {
                true
            }

            matchesCompletionFilter && matchesDateFilter
        }

        val grouped = groupTasksByDate(filteredTasks)

        val todayTasks = grouped["today"] ?: emptyList()
        val todayCompletedCount = todayTasks.count { it.isCompleted }
        val todayTotalCount = todayTasks.size
        val percentage = if (todayTotalCount > 0) {
            (todayCompletedCount * 100) / todayTotalCount
        } else {
            0
        }

        HomeState(
            todayTasks = grouped["today"] ?: emptyList(),
            tomorrowTasks = grouped["tomorrow"] ?: emptyList(),
            upcomingTasks = grouped["upcoming"] ?: emptyList(),
            completedPercentage = percentage,
            isLoading = false,
            filterType = filterType,
            filterDate = filterDate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState()
    )

    fun setCompletedFilter() {
        viewModelScope.launch {
            _filterType.value = FilterType.COMPLETED
        }
    }

    fun setUncompletedFilter() {
        viewModelScope.launch {
            _filterType.value = FilterType.UNCOMPLETED
        }
    }

    fun setDateFilter(date: Date) {
        viewModelScope.launch {
            _filterDate.value = date
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            _filterType.value = FilterType.ALL
            _filterDate.value = null
        }
    }


    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    private fun groupTasksByDate(tasks: List<Task>): Map<String, List<Task>> {
        val today = getStartOfDay(Date())
        val tomorrow = getStartOfDay(Date(today.time + 24 * 60 * 60 * 1000))
        val dayAfterTomorrow = getStartOfDay(Date(tomorrow.time + 24 * 60 * 60 * 1000))

        return tasks.groupBy { task ->
            val taskDate = getStartOfDay(task.dueDate)
            when {
                taskDate.time == today.time -> "today"
                taskDate.time == tomorrow.time -> "tomorrow"
                else -> "upcoming"
            }
        }
    }

    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}

enum class FilterType {
    ALL,
    COMPLETED,
    UNCOMPLETED
}

