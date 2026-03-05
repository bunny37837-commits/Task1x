package com.taskremind.pro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val reminderHour: Int,
    val reminderMinute: Int,
    val isEnabled: Boolean = true
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkMode: Boolean = false,
    val globalEnabled: Boolean = true
)
