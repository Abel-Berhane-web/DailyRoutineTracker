package com.abel.dailyroutinetracker

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import java.util.*

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var task: Task
    private lateinit var storageManager: StorageManager
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var countdownRunnable: Runnable

    private lateinit var statusChip: Chip
    private lateinit var countdownView: TextView
    private lateinit var checkBox: MaterialCheckBox

    // Focus Mode UI
    private lateinit var focusProgressBar: ProgressBar
    private lateinit var textFocusCountdown: TextView
    private lateinit var btnStartFocus: MaterialButton
    private var focusTimer: CountDownTimer? = null
    private var isFocusRunning = false
    private val focusTimeMillis: Long = 25 * 60 * 1000 // 25 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        storageManager = StorageManager(this)
        task = intent.getSerializableExtra("task") as Task

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val titleView = findViewById<TextView>(R.id.detailTaskTitle)
        val dateView = findViewById<TextView>(R.id.detailDate)
        val startTimeView = findViewById<TextView>(R.id.detailStartTime)
        val endTimeView = findViewById<TextView>(R.id.detailEndTime)
        val editTextNote = findViewById<EditText>(R.id.editTextNote)
        val btnSaveNote = findViewById<MaterialButton>(R.id.btnSaveNote)
        
        statusChip = findViewById(R.id.detailStatusChip)
        countdownView = findViewById(R.id.detailCountdown)
        checkBox = findViewById(R.id.detailCheckBox)

        // Focus UI Init
        focusProgressBar = findViewById(R.id.focusProgressBar)
        textFocusCountdown = findViewById(R.id.textFocusCountdown)
        btnStartFocus = findViewById(R.id.btnStartFocus)

        toolbar.setNavigationOnClickListener { finish() }

        titleView.text = task.description
        dateView.text = task.date
        startTimeView.text = "Start: ${task.time}"
        endTimeView.text = "End: ${task.endTime ?: "--:--"}"
        editTextNote.setText(task.note ?: "")
        checkBox.isChecked = task.isCompleted

        setupCountdown()
        setupFocusMode()

        btnSaveNote.setOnClickListener {
            task.note = editTextNote.text.toString().trim()
            task.isCompleted = checkBox.isChecked
            saveTaskChanges()
            finish()
        }
    }

    private fun setupFocusMode() {
        btnStartFocus.setOnClickListener {
            if (isFocusRunning) {
                stopFocusTimer()
            } else {
                startFocusTimer()
            }
        }
    }

    private fun startFocusTimer() {
        isFocusRunning = true
        btnStartFocus.text = "Stop Focus"
        btnStartFocus.setBackgroundColor(getColor(R.color.priority_red))

        focusTimer = object : CountDownTimer(focusTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textFocusCountdown.text = String.format("%02d:%02d", minutes, seconds)
                
                // Update progress bar (max is 1500 for 25 mins)
                val progress = (millisUntilFinished / 1000).toInt()
                focusProgressBar.progress = progress
            }

            override fun onFinish() {
                isFocusRunning = false
                btnStartFocus.text = "Start Focus"
                btnStartFocus.setBackgroundColor(getColor(R.color.accent_color))
                textFocusCountdown.text = "25:00"
                focusProgressBar.progress = 1500
                Toast.makeText(this@TaskDetailActivity, "Focus Session Complete!", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun stopFocusTimer() {
        focusTimer?.cancel()
        isFocusRunning = false
        btnStartFocus.text = "Start Focus"
        btnStartFocus.setBackgroundColor(getColor(R.color.accent_color))
        textFocusCountdown.text = "25:00"
        focusProgressBar.progress = 1500
    }

    private fun setupCountdown() {
        countdownRunnable = object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                val endTime = task.alarmEndTime ?: 0L

                if (task.isCompleted) {
                    statusChip.text = "CLOSED"
                    statusChip.setChipBackgroundColorResource(R.color.divider_color)
                    countdownView.text = "Task Completed"
                } else if (endTime > 0 && now < endTime) {
                    statusChip.text = "ONLINE"
                    statusChip.setChipBackgroundColorResource(R.color.primary_color)
                    
                    val diff = endTime - now
                    val hours = diff / (1000 * 60 * 60)
                    val minutes = (diff / (1000 * 60)) % 60
                    val seconds = (diff / 1000) % 60
                    
                    countdownView.text = String.format("Time left: %02d:%02d:%02d", hours, minutes, seconds)
                    handler.postDelayed(this, 1000)
                } else {
                    statusChip.text = "CLOSED"
                    statusChip.setChipBackgroundColorResource(R.color.divider_color)
                    countdownView.text = if (endTime > 0) "Time Expired" else "No end time set"
                }
            }
        }
        handler.post(countdownRunnable)
    }

    private fun saveTaskChanges() {
        val tasks = storageManager.loadTasks()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            storageManager.saveTasks(tasks)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(countdownRunnable)
        focusTimer?.cancel()
    }
}