package com.yehali.todoapp.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yehali.todoapp.data.repository.TaskRepository
import com.yehali.todoapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddEditState(
    val title: String = "",
    val description: String = "",
    val dueDate: Date = Date(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Int? = savedStateHandle["taskId"]

    private val _state = MutableStateFlow(AddEditState())
    val state: StateFlow<AddEditState> = _state.asStateFlow()

    init {
        taskId?.let { id ->
            loadTask(id)
        }
    }

    private fun loadTask(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getTaskById(id)?.let { task ->
                _state.value = _state.value.copy(
                    title = task.title,
                    description = task.description,
                    dueDate = task.dueDate,
                    isLoading = false
                )
            }
        }
    }

    fun onTitleChange(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun onDueDateChange(date: Date) {
        _state.value = _state.value.copy(dueDate = date)
    }

    fun saveTask() {
        viewModelScope.launch {
            val state = _state.value
            if (state.title.isBlank()) return@launch

            val task = Task(
                id = taskId ?: 0,
                title = state.title,
                description = state.description,
                dueDate = state.dueDate
            )

            if (taskId != null) {
                repository.updateTask(task)
            } else {
                repository.insertTask(task)
            }

            _state.value = _state.value.copy(isSaved = true)
        }
    }
}