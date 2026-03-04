package com.taskremind.pro.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(ReminderScheduler.EXTRA_TASK_ID, -1)
        if (taskId == -1) return
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(Data.Builder().putInt(ReminderScheduler.EXTRA_TASK_ID, taskId).build())
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
