package com.taskremind.pro

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskremind.pro.data.TaskEntity
import com.taskremind.pro.reminder.ReminderScheduler
import com.taskremind.pro.ui.MainViewModel
import com.taskremind.pro.ui.theme.TaskRemindTheme

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private val viewModel: MainViewModel by viewModels {
        val app = application as TaskRemindApp
        MainViewModel.Factory(app.repository, ReminderScheduler(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionsIfNeeded()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            TaskRemindTheme(darkTheme = settings.isDarkMode) {
                MainScreen(viewModel)
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun MainScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf("9") }
    var minute by remember { mutableStateOf("0") }
    var selectedTask by remember { mutableStateOf<TaskEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("TaskRemind Pro", style = MaterialTheme.typography.headlineMedium)

        PermissionGuide()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Global reminders")
            Switch(checked = settings.globalEnabled, onCheckedChange = viewModel::toggleGlobal)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Dark mode")
            Switch(checked = settings.isDarkMode, onCheckedChange = viewModel::toggleDarkMode)
        }

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task title") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = hour, onValueChange = { hour = it.filter(Char::isDigit).take(2) }, label = { Text("Hour") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = minute, onValueChange = { minute = it.filter(Char::isDigit).take(2) }, label = { Text("Minute") }, modifier = Modifier.weight(1f))
        }

        val parsedHour = hour.toIntOrNull()?.coerceIn(0, 23)
        val parsedMinute = minute.toIntOrNull()?.coerceIn(0, 59)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (title.isNotBlank() && parsedHour != null && parsedMinute != null) {
                        if (selectedTask == null) viewModel.addTask(title, parsedHour, parsedMinute)
                        else viewModel.saveTask(selectedTask!!, title, parsedHour, parsedMinute)
                        selectedTask = null
                        title = ""
                    }
                }
            ) { Text(if (selectedTask == null) "Add Task" else "Save Task") }

            if (selectedTask != null) {
                Button(onClick = { selectedTask = null; title = "" }) { Text("Cancel") }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                TaskRow(
                    task = task,
                    onToggle = { viewModel.toggleTask(task, it) },
                    onDelete = { viewModel.deleteTask(task.id) },
                    onEdit = {
                        selectedTask = task
                        title = task.title
                        hour = task.reminderHour.toString()
                        minute = task.reminderMinute.toString()
                    }
                )
            }
        }
    }
}

@Composable
private fun TaskRow(task: TaskEntity, onToggle: (Boolean) -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(task.title, style = MaterialTheme.typography.titleMedium)
            Text(String.format("Reminder: %02d:%02d", task.reminderHour, task.reminderMinute))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Switch(checked = task.isEnabled, onCheckedChange = onToggle)
                Button(onClick = onEdit) { Text("Edit") }
                Button(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}

@Composable
private fun PermissionGuide() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Permissions guide", style = MaterialTheme.typography.titleSmall)
            Text("1) Allow overlay permission")
            Text("2) Allow exact alarms and notifications")
            Text("3) Disable battery optimization for reliable reminders")
        }
    }
}
