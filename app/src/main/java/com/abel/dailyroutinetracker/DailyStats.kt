package com.abel.dailyroutinetracker

data class DailyStats(
    val date: String,
    val totalTasks: Int,
    val completedTasks: Int,
    val successRate: Int,
    val missedTasks: List<String>,
    val completedTasksList: List<String>,
    val suggestion: String,
    var isExpanded: Boolean = false // New field to track expansion state
)