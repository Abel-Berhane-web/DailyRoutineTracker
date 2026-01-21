package com.abel.dailyroutinetracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class WeeklyAnalyticsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0)
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.accent_color)
    }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.divider_color)
        alpha = 40
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_color_secondary)
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private val rectF = RectF()

    fun setData(newData: List<Int>) {
        data = newData.takeLast(7)
        if (data.size < 7) {
            val padding = List(7 - data.size) { 0 }
            data = padding + data
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val usableWidth = width.toFloat() - paddingLeft - paddingRight
        val usableHeight = height.toFloat() - paddingTop - paddingBottom - 40f
        
        val spacing = usableWidth / 7f
        val barWidth = spacing * 0.4f
        
        for (i in 0 until 7) {
            val centerX = paddingLeft + (i + 0.5f) * spacing
            val left = centerX - barWidth / 2f
            val right = centerX + barWidth / 2f
            
            // Background bar (full height)
            rectF.set(left, paddingTop.toFloat(), right, paddingTop + usableHeight)
            canvas.drawRoundRect(rectF, 12f, 12f, bgPaint)
            
            // Data bar
            val barPercent = data[i].coerceIn(0, 100) / 100f
            val barHeight = barPercent * usableHeight
            rectF.set(left, paddingTop + usableHeight - barHeight, right, paddingTop + usableHeight)
            canvas.drawRoundRect(rectF, 12f, 12f, barPaint)
            
            // Label
            val dayLabel = when(i) {
                0 -> "M"
                1 -> "T"
                2 -> "W"
                3 -> "T"
                4 -> "F"
                5 -> "S"
                6 -> "S"
                else -> ""
            }
            canvas.drawText(dayLabel, centerX, paddingTop + usableHeight + 35f, textPaint)
        }
    }
}