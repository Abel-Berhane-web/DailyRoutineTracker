package com.abel.dailyroutinetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private val tasks: MutableList<Task>,
    private val onDelete: (Int) -> Unit,
    private val onTaskClick: (Int) -> Unit,
    private val onToggleCompletion: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewPriorityStrip: View = itemView.findViewById(R.id.viewPriorityStrip)
        val checkBoxTask: CheckBox = itemView.findViewById(R.id.checkBoxTask)
        val textViewTask: TextView = itemView.findViewById(R.id.textViewTask)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.textViewTask.text = task.description
        holder.textViewTime.text = task.time
        
        holder.checkBoxTask.setOnCheckedChangeListener(null)
        holder.checkBoxTask.isChecked = task.isCompleted

        // Priority Strip Logic
        val colorResId = when {
            task.description.contains("Gym", ignoreCase = true) -> R.color.priority_blue
            task.description.contains("Workout", ignoreCase = true) -> R.color.priority_blue
            task.description.contains("Meeting", ignoreCase = true) -> R.color.priority_red
            task.description.contains("Important", ignoreCase = true) -> R.color.priority_red
            else -> R.color.priority_default
        }
        holder.viewPriorityStrip.setBackgroundColor(ContextCompat.getColor(context, colorResId))

        holder.imageViewDelete.setOnClickListener {
            onDelete(holder.adapterPosition)
        }

        holder.itemView.setOnClickListener {
            onTaskClick(holder.adapterPosition)
        }
        
        holder.checkBoxTask.setOnClickListener {
             onToggleCompletion(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = tasks.size
}