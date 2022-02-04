package com.ok.chart

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.content.ContextCompat
import com.ok.app.R
import kotlin.math.ceil

/**
 * Created by Olga Kuzmina.
 */

const val START_ANGLE = -90F

class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rect = RectF()
    private val strokeWidth: Float
    private var colors: IntArray
    private val paints: MutableList<Paint> = mutableListOf()

    private var currentAngle: Float = -1F
    private val animate: Boolean
        get() = currentAngle != -1F

    private val items: MutableList<Float> = mutableListOf()
    private var sum: Float = 0F

    private var text: String? = null
    private val textPaint: Paint
    private val textX: Float
        get() = rect.right / 2 + strokeWidth / 2
    private val textY: Float
        get() {
            val metric: Paint.FontMetrics = textPaint.fontMetrics
            val textHeight = ceil(metric.descent - metric.ascent).toInt()
            return rect.bottom / 2 + textHeight
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0).apply {
            try {
                text = getString(R.styleable.PieChart_text)
                strokeWidth = getDimension(
                    R.styleable.PieChart_strokeWidth,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics)
                )
                val colorsId: Int = getResourceId(R.styleable.PieChart_pieColors, -1)
                colors = if (colorsId != -1) {
                    resources.getIntArray(colorsId)
                } else throw Resources.NotFoundException("Please, provide colors for PieChart")

                val textSize: Float = getDimension(
                    R.styleable.PieChart_textSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.resources.displayMetrics)
                )
                val textColor: Int = getColor(R.styleable.PieChart_textColor, ContextCompat.getColor(context, android.R.color.primary_text_dark))

                textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    this.textSize = textSize
                    this.color = textColor
                    textAlign = Paint.Align.CENTER
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (animate) {
            if (currentAngle == 0F) {
                return
            }
            var angle = START_ANGLE
            var totalAngle = 0f
            for (i in items.indices) {
                val arcAngle = 360f * items[i] / sum
                totalAngle += arcAngle
                if (currentAngle >= totalAngle) {
                    // whole drawn arcs
                    canvas.drawArc(rect, angle, arcAngle, false, paints[i])
                } else if (currentAngle >= totalAngle - arcAngle) {
                    // partly drawn arcs
                    val partArcAngle = arcAngle - (totalAngle - currentAngle)
                    canvas.drawArc(rect, angle, partArcAngle, false, paints[i])
                }
                angle += arcAngle
            }
        } else {
            var angle = START_ANGLE
            for (i in items.indices) {
                val arcAngle = 360f * items[i] / sum
                canvas.drawArc(rect, angle, arcAngle, false, paints[i])
                angle += arcAngle
            }
        }
        text?.let { canvas.drawText(it, textX, textY, textPaint) }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(
            0 + strokeWidth,
            0 + strokeWidth,
            right - left - strokeWidth,
            bottom - top - strokeWidth
        )
    }

    fun setItems(items: List<Float>) {
        this.items.clear()
        this.items.addAll(items)
        sum = items.sum()

        generatePaint()
        invalidate()
    }

    fun setItems(items: List<Float>, colors: List<Int>) {
        this.colors = colors.toIntArray()
        this.items.clear()
        this.items.addAll(items)
        sum = items.sum()

        generatePaint()
        invalidate()
    }

    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    private fun generatePaint() {
        for (i in 0 until items.size) {
            paints.add(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = colors[i]
                    strokeWidth = this@PieChart.strokeWidth
                    style = Paint.Style.STROKE
                }
            )
        }
    }

    internal fun setArcAngle(angle: Float) {
        currentAngle = angle
    }
}

class AngleAnimation constructor(private val pieChart: PieChart) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val angle = 360F * interpolatedTime
        pieChart.setArcAngle(angle)
        pieChart.invalidate()
    }
}