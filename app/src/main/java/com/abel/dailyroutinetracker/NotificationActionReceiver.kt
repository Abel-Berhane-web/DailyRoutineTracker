package com.abel.dailyroutinetracker

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val taskId = intent.getLongExtra("task_id", -1)
        val taskDescription = intent.getStringExtra("task_description") ?: ""

        if (taskId == -1L) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId.toInt())

        val storageManager = StorageManager(context)
        val tasks = storageManager.loadTasks()
        val taskIndex = tasks.indexOfFirst { it.id == taskId }

        when (action) {
            "ACTION_DONE" -> {
                if (taskIndex != -1) {
                    tasks[taskIndex].isCompleted = true
                    storageManager.saveTasks(tasks)
                    Toast.makeText(context, "Task marked as done", Toast.LENGTH_SHORT).show()
                    
                    // Trigger widget update
                    val updateIntent = Intent("com.abel.dailyroutinetracker.ACTION_UPDATE_WIDGET")
                    context.sendBroadcast(updateIntent)
                }
            }
            "ACTION_SNOOZE" -> {
                val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000) // 10 minutes snooze
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                
                val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("task_description", taskDescription)
                    putExtra("task_id", taskId)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    taskId.toInt(),
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
                )
                Toast.makeText(context, "Snoozed for 10 minutes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}