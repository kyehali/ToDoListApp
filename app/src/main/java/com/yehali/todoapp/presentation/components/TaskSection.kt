package com.yehali.todoapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yehali.todoapp.domain.model.Task

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    onToggleComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onClearFilter: (() -> Unit)? = null,
    showClearButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) return

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Clear filter button - show only if needed
            if (showClearButton && onClearFilter != null) {
                TextButton(
                    onClick = onClearFilter,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        tasks.forEach { task ->
            TaskItem(
                task = task,
                onToggleComplete = { onToggleComplete(task) },
                onDelete = { onDelete(task) },
                onEdit = { onEdit(task) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}