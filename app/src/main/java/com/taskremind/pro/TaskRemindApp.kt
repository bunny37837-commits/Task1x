package com.taskremind.pro

import android.app.Application
import com.taskremind.pro.data.AppDatabase
import com.taskremind.pro.data.Repository

class TaskRemindApp : Application() {
    val database by lazy { AppDatabase.get(this) }
    val repository by lazy { Repository(database.taskDao(), database.settingsDao()) }
}
