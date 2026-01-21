package com.abel.dailyroutinetracker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StorageManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("daily_routine_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        prefs.edit().putString("tasks", json).apply()
    }

    fun loadTasks(): MutableList<Task> {
        val json = prefs.getString("tasks", null)
        return if (json != null) {
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveHistory(stats: DailyStats) {
        val history = loadHistory()
        history.add(0, stats) // Add new day to the top
        val json = gson.toJson(history)
        prefs.edit().putString("history", json).apply()
    }

    fun loadHistory(): MutableList<DailyStats> {
        val json = prefs.getString("history", null)
        return if (json != null) {
            val type = object : TypeToken<List<DailyStats>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveLastOpenDate(date: String) {
        prefs.edit().putString("last_open_date", date).apply()
    }

    fun getLastOpenDate(): String? {
        return prefs.getString("last_open_date", null)
    }

    fun saveDailyTip(tip: String, date: String) {
        prefs.edit().putString("daily_tip", tip).putString("daily_tip_date", date).apply()
    }

    fun getDailyTip(): String? {
        return prefs.getString("daily_tip", null)
    }

    fun getDailyTipDate(): String? {
        return prefs.getString("daily_tip_date", null)
    }
}