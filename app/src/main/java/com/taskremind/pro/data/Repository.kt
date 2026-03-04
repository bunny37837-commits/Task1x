package com.taskremind.pro.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Repository(
    private val taskDao: TaskDao,
    private val settingsDao: SettingsDao
) {
    val tasks: Flow<List<TaskEntity>> = taskDao.observeTasks()
    val settings: Flow<SettingsEntity> = settingsDao.observeSettings().map { it ?: SettingsEntity() }

    suspend fun addTask(title: String, hour: Int, minute: Int): TaskEntity {
        val id = taskDao.insert(TaskEntity(title = title, reminderHour = hour, reminderMinute = minute)).toInt()
        return TaskEntity(id = id, title = title, reminderHour = hour, reminderMinute = minute)
    }

    suspend fun updateTask(task: TaskEntity) = taskDao.update(task)

    suspend fun deleteTask(taskId: Int) = taskDao.delete(taskId)

    suspend fun getTask(taskId: Int) = taskDao.getTask(taskId)

    suspend fun getAllTasks() = taskDao.getAllTasks()

    suspend fun settingsNow(): SettingsEntity = settingsDao.getSettings() ?: SettingsEntity().also {
        settingsDao.upsert(it)
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        val current = settingsNow()
        settingsDao.upsert(current.copy(isDarkMode = enabled))
    }

    suspend fun updateGlobalEnabled(enabled: Boolean) {
        val current = settingsNow()
        settingsDao.upsert(current.copy(globalEnabled = enabled))
    }
}
