package com.taskremind.pro.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.taskremind.pro.MainActivity
import com.taskremind.pro.R
import com.taskremind.pro.TaskRemindApp
import com.taskremind.pro.reminder.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class OverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var countdownJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskId = intent?.getIntExtra(ReminderScheduler.EXTRA_TASK_ID, -1) ?: -1
        val title = intent?.getStringExtra(EXTRA_TASK_TITLE).orEmpty()
        if (taskId == -1 || title.isBlank()) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification(title))
        showOverlay(taskId, title)
        return START_NOT_STICKY
    }

    private fun showOverlay(taskId: Int, title: String) {
        if (overlayView != null) return
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.overlay_reminder, null)
        view.findViewById<TextView>(R.id.overlayTitle).text = title

        val progress = view.findViewById<ProgressBar>(R.id.overlayProgress)
        progress.max = 12
        progress.progress = 12

        view.findViewById<Button>(R.id.btnDone).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val app = applicationContext as TaskRemindApp
                val task = app.repository.getTask(taskId)
                task?.let { app.repository.updateTask(it.copy(isEnabled = false)) }
                ReminderScheduler(applicationContext).cancel(taskId)
            }
            dismissOverlay()
        }

        view.findViewById<Button>(R.id.btnSnooze).setOnClickListener {
            scheduleSnooze(taskId)
            dismissOverlay()
        }

        view.findViewById<Button>(R.id.btnDismiss).setOnClickListener {
            dismissOverlay()
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager?.addView(view, params)
        overlayView = view

        countdownJob?.cancel()
        countdownJob = CoroutineScope(Dispatchers.Main).launch {
            for (remaining in 11 downTo 0) {
                delay(1000)
                progress.progress = remaining
            }
            dismissOverlay()
        }
    }

    private fun scheduleSnooze(taskId: Int) {
        val triggerAt = Calendar.getInstance().apply { add(Calendar.MINUTE, 10) }.timeInMillis
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, com.taskremind.pro.reminder.AlarmReceiver::class.java)
            .putExtra(ReminderScheduler.EXTRA_TASK_ID, taskId)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    private fun dismissOverlay() {
        countdownJob?.cancel()
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        dismissOverlay()
        super.onDestroy()
    }

    private fun buildNotification(taskTitle: String): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val openAppIntent = PendingIntent.getActivity(
            this,
            1000,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Task reminder")
            .setContentText(taskTitle)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setFullScreenIntent(openAppIntent, true)
            .setContentIntent(openAppIntent)
            .build()
    }

    companion object {
        const val EXTRA_TASK_TITLE = "extra_task_title"
        private const val CHANNEL_ID = "task_reminder_channel"
        private const val NOTIFICATION_ID = 4567
    }
}
