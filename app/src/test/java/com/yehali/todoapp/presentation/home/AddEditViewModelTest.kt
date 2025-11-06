package com.yehali.todoapp.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.yehali.todoapp.data.repository.TaskRepository
import com.yehali.todoapp.domain.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelTest {

    private lateinit var repository: TaskRepository
    private lateinit var viewModel: AddEditViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        savedStateHandle = SavedStateHandle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() = runTest {
        viewModel = AddEditViewModel(repository, savedStateHandle)
        val state = viewModel.state.value
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertFalse(state.isLoading)
        assertFalse(state.isSaved)
    }

    @Test
    fun `loadTask sets state correctly`() = runTest {
        val task = Task(
            id = 1,
            title = "Existing Task",
            description = "Description",
            dueDate = Date()
        )

        whenever(repository.getTaskById(1)).thenReturn(task)
        savedStateHandle["taskId"] = 1

        viewModel = AddEditViewModel(repository, savedStateHandle)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Existing Task", state.title)
        assertEquals("Description", state.description)
        assertEquals(task.dueDate, state.dueDate)
        assertFalse(state.isLoading)
        verify(repository).getTaskById(1)
    }

    @Test
    fun `onTitleChange updates title`() = runTest {
        viewModel = AddEditViewModel(repository, savedStateHandle)
        viewModel.onTitleChange("New Title")
        assertEquals("New Title", viewModel.state.value.title)
    }

    @Test
    fun `onDescriptionChange updates description`() = runTest {
        viewModel = AddEditViewModel(repository, savedStateHandle)
        viewModel.onDescriptionChange("New Description")
        assertEquals("New Description", viewModel.state.value.description)
    }

    @Test
    fun `onDueDateChange updates date`() = runTest {
        viewModel = AddEditViewModel(repository, savedStateHandle)
        val newDate = Date(System.currentTimeMillis() + 100000)
        viewModel.onDueDateChange(newDate)
        assertEquals(newDate, viewModel.state.value.dueDate)
    }

    @Test
    fun `saveTask inserts new task when taskId is null`() = runTest {
        viewModel = AddEditViewModel(repository, savedStateHandle)
        viewModel.onTitleChange("New Task")
        viewModel.onDescriptionChange("Description")
        val date = Date()
        viewModel.onDueDateChange(date)

        viewModel.saveTask()
        advanceUntilIdle()

        verify(repository).insertTask(
            argThat {
                title == "New Task" &&
                        description == "Description" &&
                        dueDate == date
            }
        )
        assertTrue(viewModel.state.value.isSaved)
    }

    @Test
    fun `saveTask does nothing when title is blank`() = runTest {
        viewModel = AddEditViewModel(repository, savedStateHandle)
        viewModel.onTitleChange("")

        viewModel.saveTask()
        advanceUntilIdle()

        verify(repository, never()).insertTask(any())
        verify(repository, never()).updateTask(any())
        assertFalse(viewModel.state.value.isSaved)
    }
}
