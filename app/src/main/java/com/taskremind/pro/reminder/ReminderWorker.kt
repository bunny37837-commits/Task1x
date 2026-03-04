package com.taskremind.pro.reminder

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.taskremind.pro.TaskRemindApp
import com.taskremind.pro.overlay.OverlayService

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getInt(ReminderScheduler.EXTRA_TASK_ID, -1)
        if (taskId == -1) return Result.failure()
        val app = applicationContext as TaskRemindApp
        val settings = app.repository.settingsNow()
        if (!settings.globalEnabled) return Result.success()
        val task = app.repository.getTask(taskId) ?: return Result.success()
        if (!task.isEnabled) return Result.success()

        val intent = Intent(applicationContext, OverlayService::class.java).apply {
            putExtra(ReminderScheduler.EXTRA_TASK_ID, task.id)
            putExtra(OverlayService.EXTRA_TASK_TITLE, task.title)
        }
        ContextCompat.startForegroundService(applicationContext, intent)

        ReminderScheduler(applicationContext).schedule(task)
        return Result.success()
    }
}
