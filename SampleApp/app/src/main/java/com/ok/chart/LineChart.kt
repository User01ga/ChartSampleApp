package com.ok.chart

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.ok.app.R

/**
 * Created by Olga Kuzmina.
 */
class LineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rect = RectF()
    private val rectPath = Path()
    private val paints: MutableList<Paint> = mutableListOf()

    private var progress: Float = -1F
    private val animate: Boolean
        get() = progress != -1F

    private val items: MutableList<Float> = mutableListOf()
    private var sum: Float = 0F

    private val isRoundCorner: Boolean
    private var colors: IntArray

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LineChart, 0, 0).apply {
            try {
                isRoundCorner = getBoolean(R.styleable.LineChart_roundCorner, false)
                val colorsId: Int = getResourceId(R.styleable.LineChart_lineColors, -1)
                colors = if (colorsId != -1) {
                    resources.getIntArray(colorsId)
                } else throw Resources.NotFoundException("Please, provide colors for LineChart")
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isRoundCorner) {
            canvas.clipPath(rectPath)
        }

        if (animate) {
            var offset = 0f
            for (i in items.indices) {
                val width = rect.width() * items[i] / sum
                if (progress >= offset + width) {
                    // whole drawn rect
                    canvas.drawRect(offset, 0f, offset + width, rect.height(), paints[i])
                } else if (progress > offset) {
                    // partly drawn rect
                    val progressWidth = offset + progress
                    canvas.drawRect(offset, 0f, offset + progressWidth, rect.height(), paints[i])
                }
                offset += width
            }
        } else {
            var offset = 0f
            for (i in items.indices) {
                val width = rect.width() * items[i] / sum
                canvas.drawRect(offset, 0f, offset + width, rect.height(), paints[i])
                offset += width
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val measuredWidth = (right - left).toFloat()
        val measuredHeight = (bottom - top).toFloat()
        rect.set(0F, 0F, measuredWidth, measuredHeight)
        if (isRoundCorner) {
            rectPath.reset()
            rectPath.addRoundRect(rect, measuredHeight / 2, measuredHeight / 2, Path.Direction.CW)
        }
    }

    fun setItems(items: List<Float>, colors: List<Int>) {
        this.colors = colors.toIntArray()
        this.items.clear()
        this.items.addAll(items)
        sum = this.items.sum()

        generatePaint()
        invalidate()
    }

    fun setItems(items: List<Float>) {
        this.items.clear()
        this.items.addAll(items)
        sum = this.items.sum()

        generatePaint()
        invalidate()
    }

    private fun generatePaint() {
        for (i in 0 until items.size) {
            paints.add(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = colors[i]
                }
            )
        }
    }

    internal fun setProgress(progress: Float) {
        this.progress = progress
    }
}

class ProgressAnimation constructor(private val lineChart: LineChart) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val progress = lineChart.width * interpolatedTime
        lineChart.setProgress(progress)
        lineChart.invalidate()
    }
}