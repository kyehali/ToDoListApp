package com.yehali.todoapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yehali.todoapp.presentation.components.TaskSection
import android.app.DatePickerDialog
import java.util.Calendar




// Custom Funnel/Filter Icon
private val FilterIcon: ImageVector
    get() {
        return ImageVector.Builder(
            name = "Filter",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Funnel shape path
                moveTo(10f, 18f)
                lineTo(14f, 18f)
                lineTo(14f, 16f)
                lineTo(10f, 16f)
                close()
                moveTo(3f, 6f)
                lineTo(3f, 8f)
                lineTo(21f, 8f)
                lineTo(21f, 6f)
                close()
                moveTo(6f, 13f)
                lineTo(18f, 13f)
                lineTo(18f, 11f)
                lineTo(6f, 11f)
                close()
            }
        }.build()
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddTask: () -> Unit,
    onEditTask: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showClearButtonOnToday = state.todayTasks.isNotEmpty() && (state.filterType != FilterType.ALL || state.filterDate != null)
    val showClearButtonOnTomorrow = state.todayTasks.isEmpty() && state.tomorrowTasks.isNotEmpty() && (state.filterType != FilterType.ALL || state.filterDate != null)
    val showClearButtonOnUpcoming = state.todayTasks.isEmpty() && state.tomorrowTasks.isEmpty() && state.upcomingTasks.isNotEmpty() && (state.filterType != FilterType.ALL || state.filterDate != null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // Filter Icon Button
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = FilterIcon,
                            contentDescription = "Filter tasks",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Dropdown Menu for Filter Options
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        // Completed Tasks Option
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Completed",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = {
                                viewModel.setCompletedFilter()
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null
                                )
                            }
                        )

                        // Uncompleted Tasks Option
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Uncompleted",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = {
                                viewModel.setUncompletedFilter()
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null
                                )
                            }
                        )

                        Divider()

                        // Date Filter Option
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Filter by Date",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = {
                                showFilterMenu = false

                                val calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        calendar.set(year, month, day)
                                        viewModel.setDateFilter(calendar.time)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Completion percentage card
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CompletionCard(
                        percentage = state.completedPercentage,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Today's tasks
                item {
                    TaskSection(
                        title = "Today",
                        tasks = state.todayTasks,
                        onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                        onDelete = { viewModel.deleteTask(it) },
                        onEdit = { onEditTask(it.id) },
                        onClearFilter = { viewModel.clearFilters() },
                        showClearButton = showClearButtonOnToday
                    )
                }

                item {
                    TaskSection(
                        title = "Tomorrow",
                        tasks = state.tomorrowTasks,
                        onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                        onDelete = { viewModel.deleteTask(it) },
                        onEdit = { onEditTask(it.id) },
                        onClearFilter = { viewModel.clearFilters() },
                        showClearButton = showClearButtonOnTomorrow
                    )
                }


                item {
                    TaskSection(
                        title = "Upcoming",
                        tasks = state.upcomingTasks,
                        onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                        onDelete = { viewModel.deleteTask(it) },
                        onEdit = { onEditTask(it.id) },
                        onClearFilter = { viewModel.clearFilters() },
                        showClearButton = showClearButtonOnUpcoming
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun CompletionCard(percentage: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today's Completion",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}