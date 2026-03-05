package com.taskremind.pro.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.taskremind.pro.TaskRemindApp

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val app = context.applicationContext as TaskRemindApp
        val scheduler = ReminderScheduler(context)
        CoroutineScope(Dispatchers.IO).launch {
            val settings = app.repository.settingsNow()
            if (!settings.globalEnabled) return@launch
            app.repository.getAllTasks().filter { it.isEnabled }.forEach { scheduler.schedule(it) }
        }
    }
}
