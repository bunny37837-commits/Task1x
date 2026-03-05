package com.taskremind.pro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taskremind.pro.data.Repository
import com.taskremind.pro.data.SettingsEntity
import com.taskremind.pro.data.TaskEntity
import com.taskremind.pro.reminder.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.tasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val settings: StateFlow<SettingsEntity> = repository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SettingsEntity()
    )

    fun addTask(title: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            val task = repository.addTask(title, hour, minute)
            if (settings.value.globalEnabled) scheduler.schedule(task)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            scheduler.cancel(taskId)
        }
    }

    fun toggleTask(task: TaskEntity, enabled: Boolean) {
        viewModelScope.launch {
            val updated = task.copy(isEnabled = enabled)
            repository.updateTask(updated)
            if (enabled && settings.value.globalEnabled) scheduler.schedule(updated) else scheduler.cancel(updated.id)
        }
    }

    fun saveTask(task: TaskEntity, title: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            val updated = task.copy(title = title, reminderHour = hour, reminderMinute = minute)
            repository.updateTask(updated)
            if (updated.isEnabled && settings.value.globalEnabled) scheduler.schedule(updated)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.updateDarkMode(enabled) }
    }

    fun toggleGlobal(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateGlobalEnabled(enabled)
            val tasks = repository.getAllTasks()
            if (enabled) tasks.filter { it.isEnabled }.forEach { scheduler.schedule(it) }
            else tasks.forEach { scheduler.cancel(it.id) }
        }
    }

    class Factory(
        private val repository: Repository,
        private val scheduler: ReminderScheduler
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository, scheduler) as T
        }
    }
}
