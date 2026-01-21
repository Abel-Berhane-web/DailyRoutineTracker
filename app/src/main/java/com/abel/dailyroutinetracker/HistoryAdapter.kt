package com.abel.dailyroutinetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

class HistoryAdapter(
    context: Context,
    private val history: List<DailyStats>,
    private val onAnalyze: (DailyStats) -> Unit
) : ArrayAdapter<DailyStats>(context, 0, history) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false)

        val stats = getItem(position)
        
        val layoutHeader = view.findViewById<LinearLayout>(R.id.layoutHeader)
        val layoutDetails = view.findViewById<LinearLayout>(R.id.layoutDetails)
        val textViewDate = view.findViewById<TextView>(R.id.textViewDate)
        val textViewSuccessRate = view.findViewById<TextView>(R.id.textViewSuccessRate)
        val progressBarHistory = view.findViewById<ProgressBar>(R.id.progressBarHistory)
        val textViewSuggestion = view.findViewById<TextView>(R.id.textViewSuggestion)
        val textViewMissed = view.findViewById<TextView>(R.id.textViewMissed)
        val textViewCompleted = view.findViewById<TextView>(R.id.textViewCompleted)
        val imageViewExpand = view.findViewById<ImageView>(R.id.imageViewExpand)
        val btnAnalyzeAI = view.findViewById<Button>(R.id.btnAnalyzeAI)

        stats?.let { item ->
            textViewDate.text = item.date
            textViewSuccessRate.text = "${item.successRate}%"
            progressBarHistory.progress = item.successRate
            textViewSuggestion.text = item.suggestion
            
            textViewMissed.text = if (item.missedTasks.isEmpty()) "None" else item.missedTasks.joinToString("\n", prefix = "")
            textViewCompleted.text = if (item.completedTasksList.isEmpty()) "None" else item.completedTasksList.joinToString("\n", prefix = "")
            
            layoutDetails.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            imageViewExpand.rotation = if (item.isExpanded) 180f else 0f
            
            layoutHeader.setOnClickListener {
                item.isExpanded = !item.isExpanded
                notifyDataSetChanged()
            }

            btnAnalyzeAI.setOnClickListener {
                onAnalyze(item)
            }
        }

        return view
    }
}