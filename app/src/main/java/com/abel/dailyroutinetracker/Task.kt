package com.abel.dailyroutinetracker

import java.io.Serializable

data class Task(
    val description: String,
    val time: String, // Start time string for UI
    val endTime: String? = null, // End time string
    var isCompleted: Boolean = false,
    val date: String, // Date string (yyyy-MM-dd)
    val alarmTime: Long? = null, // Start time in millis
    val alarmEndTime: Long? = null, // End time in millis
    var note: String? = null,
    val category: String = "General", // Default category
    val id: Long = System.currentTimeMillis()
) : Serializable