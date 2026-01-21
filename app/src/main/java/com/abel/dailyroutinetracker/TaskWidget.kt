package com.abel.dailyroutinetracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

class TaskWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE || 
            intent.action == "com.abel.dailyroutinetracker.ACTION_UPDATE_WIDGET") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, TaskWidget::class.java))
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.task_widget)
            
            // Intent to open the app when clicking the widget
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Load pending tasks
            val storageManager = StorageManager(context)
            val pendingTasks = storageManager.loadTasks().filter { !it.isCompleted }.take(3)

            if (pendingTasks.isEmpty()) {
                views.setTextViewText(R.id.widget_task_1, "• No pending tasks")
                views.setViewVisibility(R.id.widget_task_2, View.GONE)
                views.setViewVisibility(R.id.widget_task_3, View.GONE)
            } else {
                views.setTextViewText(R.id.widget_task_1, "• ${pendingTasks[0].description}")
                
                if (pendingTasks.size > 1) {
                    views.setViewVisibility(R.id.widget_task_2, View.VISIBLE)
                    views.setTextViewText(R.id.widget_task_2, "• ${pendingTasks[1].description}")
                } else {
                    views.setViewVisibility(R.id.widget_task_2, View.GONE)
                }

                if (pendingTasks.size > 2) {
                    views.setViewVisibility(R.id.widget_task_3, View.VISIBLE)
                    views.setTextViewText(R.id.widget_task_3, "• ${pendingTasks[2].description}")
                } else {
                    views.setViewVisibility(R.id.widget_task_3, View.GONE)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}