package com.abel.dailyroutinetracker

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI Containers
    private lateinit var contentHome: ScrollView
    private lateinit var contentTasks: LinearLayout
    private lateinit var contentHistory: LinearLayout
    private lateinit var contentAbout: LinearLayout
    private lateinit var contentAssistant: LinearLayout

    // Dashboard UI
    private lateinit var textGreeting: TextView
    private lateinit var textPendingCount: TextView
    private lateinit var imageProfileGreeting: ImageView
    private lateinit var textTotalTasks: TextView
    private lateinit var textCompletedTasks: TextView
    private lateinit var progressBarLarge: ProgressBar
    private lateinit var textProgressPercentage: TextView
    private lateinit var textAIAdvice: TextView
    private lateinit var switchTheme: SwitchMaterial
    private lateinit var cardViewHistory: MaterialCardView
    private lateinit var cardQuickAdd: MaterialCardView
    private lateinit var weeklyAnalyticsView: WeeklyAnalyticsView

    // Tasks UI
    private lateinit var editTextTask: EditText
    private var buttonAddTask: Button? = null
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var fabAddTask: ExtendedFloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewProgress: TextView

    // Assistant UI
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var editTextChatMessage: EditText
    private lateinit var btnSendChat: Button
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    // About UI
    private lateinit var cardYoutube: MaterialCardView
    private lateinit var cardInstagram: MaterialCardView
    private lateinit var cardTelegram: MaterialCardView
    private lateinit var cardEmail: MaterialCardView

    // Adapters & Data
    private lateinit var adapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<DailyStats>()
    private lateinit var storageManager: StorageManager
    private lateinit var prefs: SharedPreferences

    private var profileDialog: Dialog? = null
    private var dialogProfileImage: ImageView? = null

    // Groq API Configuration
    private val GROQ_API_KEY: String
        get() = "Bearer ${BuildConfig.GROQ_API_KEY}"
    private val GROQ_MODEL = "openai/gpt-oss-20b"

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            prefs.edit().putString("user_image_uri", it.toString()).apply()
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            updateGreeting()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefs = getSharedPreferences("daily_routine_prefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("is_dark_mode", true)
        applyTheme(isDarkMode)
        
        setContentView(R.layout.activity_main)

        storageManager = StorageManager(this)

        initializeViews()
        setupAdapters()
        setupListeners()
        setupSwipeGestures()
        setupChat()
        
        checkNewDay()
        loadData()
        updateDashboard()
        updateGreeting()
        checkNotificationPermission()
        fetchAIAdvice()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        updateDashboard()
        updateProgress()
    }

    private fun initializeViews() {
        contentHome = findViewById(R.id.contentHome)
        contentTasks = findViewById(R.id.contentTasks)
        contentHistory = findViewById(R.id.contentHistory)
        contentAbout = findViewById(R.id.contentAbout)
        contentAssistant = findViewById(R.id.contentAssistant)

        textGreeting = findViewById(R.id.textGreeting)
        textPendingCount = findViewById(R.id.textPendingCount)
        imageProfileGreeting = findViewById(R.id.imageProfileGreeting)
        textTotalTasks = findViewById(R.id.textTotalTasks)
        textCompletedTasks = findViewById(R.id.textCompletedTasks)
        progressBarLarge = findViewById(R.id.progressBarLarge)
        textProgressPercentage = findViewById(R.id.textProgressPercentage)
        textAIAdvice = findViewById(R.id.textAIAdvice)
        switchTheme = findViewById(R.id.switchTheme)
        cardViewHistory = findViewById(R.id.cardViewHistory)
        cardQuickAdd = findViewById(R.id.cardQuickAdd)
        weeklyAnalyticsView = findViewById(R.id.weeklyAnalyticsView)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAddTask = findViewById(R.id.buttonAddTask)
        recyclerViewTasks = findViewById(R.id.listViewTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        progressBar = findViewById(R.id.progressBar)
        textViewProgress = findViewById(R.id.textViewProgress)

        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        editTextChatMessage = findViewById(R.id.editTextChatMessage)
        btnSendChat = findViewById(R.id.btnSendChat)

        cardYoutube = findViewById(R.id.cardYoutube)
        cardInstagram = findViewById(R.id.cardInstagram)
        cardTelegram = findViewById(R.id.cardTelegram)
        cardEmail = findViewById(R.id.cardEmail)

        switchTheme.isChecked = !prefs.getBoolean("is_dark_mode", true)
    }

    private fun setupAdapters() {
        adapter = TaskAdapter(this, taskList, 
            onDelete = { position -> deleteTask(position) },
            onTaskClick = { position -> openTaskDetail(position) },
            onToggleCompletion = { position -> toggleTaskCompletion(position) }
        )
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = adapter

        historyAdapter = HistoryAdapter(this, historyList) { stats ->
            analyzeHistoryWithAI(stats)
        }
        findViewById<ListView>(R.id.listViewHistory).adapter = historyAdapter

        chatAdapter = ChatAdapter(chatMessages)
        recyclerViewChat.layoutManager = LinearLayoutManager(this)
        recyclerViewChat.adapter = chatAdapter
    }

    private fun openTaskDetail(position: Int) {
        val task = taskList[position]
        val intent = Intent(this, TaskDetailActivity::class.java)
        intent.putExtra("task", task)
        startActivity(intent)
    }

    private fun setupChat() {
        if (chatMessages.isEmpty()) {
            val userName = prefs.getString("user_name", "User")
            chatMessages.add(ChatMessage("Hello $userName! I'm your Routine Assistant. How can I help you today?", false))
            chatAdapter.notifyItemInserted(0)
        }
    }

    private fun setupSwipeGestures() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    deleteTask(position)
                } else {
                    Toast.makeText(this@MainActivity, "Edit feature coming soon!", Toast.LENGTH_SHORT).show()
                    adapter.notifyItemChanged(position)
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerViewTasks)
    }

    private fun setupListeners() {
        buttonAddTask?.setOnClickListener {
            showDatePickerAndAddTask()
        }
        
        fabAddTask.setOnClickListener {
            showTasks()
            updateBottomNav(R.id.nav_tasks)
            editTextTask.requestFocus()
        }
        
        cardQuickAdd.setOnClickListener {
            showTasks()
            updateBottomNav(R.id.nav_tasks)
            editTextTask.requestFocus()
        }
        
        cardViewHistory.setOnClickListener {
            showHistory()
            updateBottomNav(R.id.nav_history)
        }
        
        imageProfileGreeting.setOnClickListener {
            showProfileDialog()
        }
        
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            val newIsDarkMode = !isChecked
            prefs.edit().putBoolean("is_dark_mode", newIsDarkMode).apply()
            recreate()
        }

        btnSendChat.setOnClickListener {
            sendChatMessage()
        }
        
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showHome()
                    true
                }
                R.id.nav_tasks -> {
                    showTasks()
                    true
                }
                R.id.nav_assistant -> {
                    showAssistant()
                    true
                }
                R.id.nav_history -> {
                    showHistory()
                    true
                }
                R.id.nav_info -> {
                    showAbout()
                    true
                }
                else -> false
            }
        }

        cardYoutube.setOnClickListener { openUrl("https://www.youtube.com/") }
        cardInstagram.setOnClickListener { openUrl("https://www.instagram.com/abel.berhane.yemane") }
        cardTelegram.setOnClickListener { openUrl("https://t.me/Aton_B") }
        cardEmail.setOnClickListener { sendEmail("abelabelberhane1993@gmail.com") }
    }

    private fun updateBottomNav(id: Int) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = id
    }

    private fun sendChatMessage() {
        val text = editTextChatMessage.text.toString().trim()
        if (text.isEmpty()) return

        chatMessages.add(ChatMessage(text, true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        recyclerViewChat.scrollToPosition(chatMessages.size - 1)
        editTextChatMessage.text.clear()

        val userName = prefs.getString("user_name", "User")
        val pendingCount = taskList.count { !it.isCompleted }
        val taskDescriptions = taskList.joinToString(", ") { it.description }

        lifecycleScope.launch {
            try {
                val promptText = "User Name: $userName. Pending: $pendingCount ($taskDescriptions). Message: $text. Respond as a helpful routine coach for the 'DO IT' app. IMPORTANT: Only give answers based on the app and the user's routine. Do not provide additional or unrelated info. Provide a professional, structured output. Use headers for sections. If providing a routine, format it as a clean list with times. Use simple text for structure. Do not use markdown like '**' or symbols like '__'. Keep it visually engaging and very readable."
                val request = ChatRequest(
                    model = GROQ_MODEL,
                    messages = listOf(Message("user", promptText))
                )
                
                val response = withContext(Dispatchers.IO) {
                    GroqClient.api.chatCompletion(GROQ_API_KEY, request)
                }
                
                val reply = response.choices[0].message.content
                chatMessages.add(ChatMessage(formatAIResponse(reply).toString(), false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerViewChat.scrollToPosition(chatMessages.size - 1)
            } catch (e: Exception) {
                e.printStackTrace()
                chatMessages.add(ChatMessage("Error: ${e.localizedMessage ?: "Connection failed"}", false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerViewChat.scrollToPosition(chatMessages.size - 1)
            }
        }
    }

    private fun formatAIResponse(input: String): CharSequence {
        // Strip out common markdown symbols
        val cleaned = input.replace("**", "").replace("__", "").replace("#", "").trim()
        return cleaned
    }

    private fun fetchAIAdvice() {
        val savedTip = storageManager.getDailyTip()
        val savedDate = storageManager.getDailyTipDate()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (savedTip != null && savedDate == currentDate) {
            textAIAdvice.text = savedTip
            return
        }

        textAIAdvice.text = "Consulting the Routine Assistant..."
        val userName = prefs.getString("user_name", "User")
        val pendingCount = taskList.count { !it.isCompleted }
        
        lifecycleScope.launch {
            try {
                val promptText = "Give a very brief, professional, and motivational routine advice for $userName. They have $pendingCount pending tasks today. Keep it under 2 sentences. No markdown symbols."
                val request = ChatRequest(
                    model = GROQ_MODEL,
                    messages = listOf(Message("user", promptText))
                )
                
                val response = withContext(Dispatchers.IO) {
                    GroqClient.api.chatCompletion(GROQ_API_KEY, request)
                }
                
                val reply = formatAIResponse(response.choices[0].message.content).toString()
                textAIAdvice.text = reply
                storageManager.saveDailyTip(reply, currentDate)
            } catch (e: Exception) {
                e.printStackTrace()
                textAIAdvice.text = "Stay focused and keep crushing your goals!"
            }
        }
    }

    private fun analyzeHistoryWithAI(stats: DailyStats) {
        Toast.makeText(this, "Analyzing performance...", Toast.LENGTH_SHORT).show()
        
        lifecycleScope.launch {
            try {
                val promptText = """
                    Analyze this daily performance for a user:
                    Date: ${stats.date}
                    Total Tasks: ${stats.totalTasks}
                    Completed: ${stats.completedTasks}
                    Success Rate: ${stats.successRate}%
                    Missed Tasks: ${stats.missedTasks.joinToString(", ")}
                    
                    Provide a brief, supportive insight and one specific tip to improve tomorrow. 
                    Be professional and concise. No markdown symbols.
                """.trimIndent()

                val request = ChatRequest(
                    model = GROQ_MODEL,
                    messages = listOf(Message("user", promptText))
                )
                
                val response = withContext(Dispatchers.IO) {
                    GroqClient.api.chatCompletion(GROQ_API_KEY, request)
                }
                
                showAIDialog("History Insight", formatAIResponse(response.choices[0].message.content).toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "AI Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAIDialog(title: String, message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_DailyRoutinetracker)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun showDatePickerAndAddTask() {
        val taskDescription = editTextTask.text.toString().trim()
        if (taskDescription.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_enter_task), Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            showStartTimePicker(taskDescription, selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showStartTimePicker(description: String, date: Calendar) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            val startCal = date.clone() as Calendar
            startCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            startCal.set(Calendar.MINUTE, minute)
            startCal.set(Calendar.SECOND, 0)
            
            showEndTimePicker(description, startCal)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
    }

    private fun showEndTimePicker(description: String, startCal: Calendar) {
        TimePickerDialog(this, { _, hourOfDay, minute ->
            val endCal = startCal.clone() as Calendar
            endCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            endCal.set(Calendar.MINUTE, minute)
            
            addTaskWithFullDetails(description, startCal.timeInMillis, endCal.timeInMillis)
        }, startCal.get(Calendar.HOUR_OF_DAY) + 1, startCal.get(Calendar.MINUTE), false).show()
    }

    private fun addTaskWithFullDetails(description: String, startTime: Long, endTime: Long) {
        val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(startTime))
        val endTimeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(endTime))
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startTime))
        
        val newTask = Task(description, timeString, endTimeString, false, dateString, startTime, endTime)
        
        taskList.add(newTask)
        adapter.notifyItemInserted(taskList.size - 1)
        editTextTask.text.clear()
        
        scheduleAlarm(newTask)
        
        updateProgress()
        updateDashboard()
        saveData()
        updateWeeklyAnalytics() // Update chart
        Toast.makeText(this, "Task Added for $dateString", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleAlarm(task: Task) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("task_description", task.description)
            putExtra("task_id", task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = task.alarmTime ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                val intentPermission = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intentPermission)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "Daily Routine Tracker Feedback")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showProfileDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_profile, null)
        dialog.setContentView(view)
        
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val dialogProfileImage = view.findViewById<ImageView>(R.id.dialogProfileImage)
        val dialogProfileName = view.findViewById<TextView>(R.id.dialogProfileName)
        val btnChangeProfileImage = view.findViewById<Button>(R.id.btnChangeProfileImage)
        
        val name = prefs.getString("user_name", "User")
        dialogProfileName.text = name
        
        val imageUriString = prefs.getString("user_image_uri", null)
        if (imageUriString != null) {
            try {
                val uri = Uri.parse(imageUriString)
                Glide.with(this)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(dialogProfileImage!!)
                dialogProfileImage!!.setPadding(0, 0, 0, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        btnChangeProfileImage.setOnClickListener {
            pickImage.launch("image/*")
        }
        
        profileDialog = dialog
        dialog.show()
    }
    
    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun checkNewDay() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastOpenDate = storageManager.getLastOpenDate()

        if (lastOpenDate != null && lastOpenDate != currentDate) {
            val tasksToArchive = storageManager.loadTasks()
            if (tasksToArchive.isNotEmpty()) {
                val total = tasksToArchive.size
                val completed = tasksToArchive.count { it.isCompleted }
                val successRate = if (total > 0) (completed * 100) / total else 0
                
                val missedTasks = tasksToArchive.filter { !it.isCompleted }.map { it.description }
                val completedTasksList = tasksToArchive.filter { it.isCompleted }.map { it.description }
                
                val dailyStats = DailyStats(
                    date = lastOpenDate,
                    totalTasks = total,
                    completedTasks = completed,
                    successRate = successRate,
                    missedTasks = missedTasks,
                    completedTasksList = completedTasksList,
                    suggestion = "Keep pushing forward!"
                )

                storageManager.saveHistory(dailyStats)
                
                taskList.clear()
                adapter.notifyDataSetChanged()
                saveData()
            }
        }
        storageManager.saveLastOpenDate(currentDate)
    }

    private fun loadData() {
        taskList.clear()
        taskList.addAll(storageManager.loadTasks())
        adapter.notifyDataSetChanged()
        updateProgress()
        
        historyList.clear()
        historyList.addAll(storageManager.loadHistory())
        historyAdapter.notifyDataSetChanged()
        
        updateWeeklyAnalytics()
    }

    private fun updateWeeklyAnalytics() {
        // Get rates from last 6 days history
        val historyRates = historyList.take(6).reversed().map { it.successRate }
        
        // Calculate today's current rate
        val total = taskList.size
        val comp = taskList.count { it.isCompleted }
        val todayRate = if (total > 0) (comp * 100) / total else 0
        
        // Combine history + today for a full 7-day trend
        val weeklyData = historyRates + todayRate
        
        weeklyAnalyticsView.setData(weeklyData)
    }

    private fun saveData() {
        storageManager.saveTasks(taskList)
    }

    private fun showHome() {
        hideAllContainers()
        contentHome.visibility = View.VISIBLE
        fabAddTask.show()
        updateDashboard()
        updateGreeting()
        updateWeeklyAnalytics()
    }

    private fun showTasks() {
        hideAllContainers()
        contentTasks.visibility = View.VISIBLE
        fabAddTask.hide()
    }

    private fun showHistory() {
        hideAllContainers()
        contentHistory.visibility = View.VISIBLE
        fabAddTask.hide()
        
        historyList.clear()
        historyList.addAll(storageManager.loadHistory())
        historyAdapter.notifyDataSetChanged()
    }

    private fun showAbout() {
        hideAllContainers()
        contentAbout.visibility = View.VISIBLE
        fabAddTask.hide()
    }

    private fun showAssistant() {
        hideAllContainers()
        contentAssistant.visibility = View.VISIBLE
        fabAddTask.hide()
    }

    private fun hideAllContainers() {
        contentHome.visibility = View.GONE
        contentTasks.visibility = View.GONE
        contentHistory.visibility = View.GONE
        contentAbout.visibility = View.GONE
        contentAssistant.visibility = View.GONE
    }

    private fun deleteTask(position: Int) {
        if (position in 0 until taskList.size) {
            val task = taskList[position]
            cancelAlarm(task)
            taskList.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateProgress()
            updateDashboard()
            saveData()
            updateWeeklyAnalytics()
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelAlarm(task: Task) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
    
    private fun toggleTaskCompletion(position: Int) {
        if (position in 0 until taskList.size) {
            val task = taskList[position]
            task.isCompleted = !task.isCompleted
            adapter.notifyItemChanged(position)
            updateProgress()
            updateDashboard()
            saveData()
            updateWeeklyAnalytics() // Update chart live
        }
    }

    private fun updateProgress() {
        val totalTasks = taskList.size
        val completedCount = taskList.count { it.isCompleted }

        if (totalTasks > 0) {
            val percentage = (completedCount * 100) / totalTasks
            progressBar.progress = percentage
            textViewProgress.text = getString(R.string.progress_format, percentage)
        } else {
            progressBar.progress = 0
            textViewProgress.text = getString(R.string.progress_zero)
        }
    }

    private fun updateDashboard() {
        val total = taskList.size
        val completed = taskList.count { it.isCompleted }
        
        textTotalTasks.text = total.toString()
        textCompletedTasks.text = completed.toString()
        
        val percentage = if (total > 0) (completed * 100) / total else 0
        progressBarLarge.progress = percentage
        textProgressPercentage.text = "$percentage%"
        
        val pending = total - completed
        textPendingCount.text = "You have $pending tasks pending today."
    }

    private fun updateGreeting() {
        val name = prefs.getString("user_name", "User") ?: "User"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greet = when(hour) { in 0..11 -> "Good Morning"; in 12..17 -> "Good Afternoon"; else -> "Good Evening" }
        textGreeting.text = "$greet, $name!"
        prefs.getString("user_image_uri", null)?.let {
            Glide.with(this).load(Uri.parse(it)).apply(RequestOptions.circleCropTransform()).into(imageProfileGreeting)
        }
    }
}