package com.example.expenzo.Utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.cos
import kotlin.math.sin

class BeautifulCircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Colors
    private val primaryOrange = Color.parseColor("#FF6B35")
    private val secondaryOrange = Color.parseColor("#FF8C42")
    private val lightOrange = Color.parseColor("#FFB366")
    private val backgroundGray = Color.parseColor("#F0F0F0")
    private val textColor = Color.parseColor("#2C2C2C")
    private val shadowColor = Color.parseColor("#40000000")

    // Paint objects
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Properties
    private var progress = 0f
    private var maxProgress = 100f
    private var strokeWidth = 24f
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f

    // Animation
    private var animator: ValueAnimator? = null
    private var animatedProgress = 0f

    // Gradient and effects
    private var progressGradient: SweepGradient? = null
    private var glowGradient: RadialGradient? = null

    init {
        setupPaints()
    }

    private fun setupPaints() {
        // Background circle paint
        backgroundPaint.apply {
            color = backgroundGray
            style = Paint.Style.STROKE
            strokeWidth = this@BeautifulCircularProgressBar.strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        // Progress circle paint
        progressPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@BeautifulCircularProgressBar.strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        // Text paint
        textPaint.apply {
            color = textColor
            textAlign = Paint.Align.CENTER
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Shadow paint
        shadowPaint.apply {
            color = shadowColor
            style = Paint.Style.STROKE
            strokeWidth = this@BeautifulCircularProgressBar.strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        // Glow paint
        glowPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@BeautifulCircularProgressBar.strokeWidth + 8f
            strokeCap = Paint.Cap.ROUND
            alpha = 80
        }

        // Dot paint for progress indicator
        dotPaint.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            setShadowLayer(8f, 0f, 4f, shadowColor)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 2f
        centerY = h / 2f
        radius = (minOf(w, h) / 2f) - strokeWidth - 20f

        setupGradients()
    }

    private fun setupGradients() {
        // Progress gradient
        progressGradient = SweepGradient(
            centerX, centerY,
            intArrayOf(primaryOrange, secondaryOrange, lightOrange, primaryOrange),
            floatArrayOf(0f, 0.3f, 0.7f, 1f)
        )

        // Glow gradient
        glowGradient = RadialGradient(
            centerX, centerY, radius + 20f,
            intArrayOf(lightOrange, Color.TRANSPARENT),
            floatArrayOf(0.7f, 1f),
            Shader.TileMode.CLAMP
        )

        progressPaint.shader = progressGradient
        glowPaint.shader = glowGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw glow effect
        canvas.drawCircle(centerX, centerY, radius, glowPaint)

        // Draw shadow
        canvas.save()
        canvas.translate(4f, 4f)
        canvas.drawCircle(centerX, centerY, radius, shadowPaint)
        canvas.restore()

        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Draw progress arc
        val sweepAngle = (animatedProgress / maxProgress) * 360f
        val startAngle = -90f // Start from top

        if (sweepAngle > 0) {
            val rect = RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )
            canvas.drawArc(rect, startAngle, sweepAngle, false, progressPaint)

            // Draw progress indicator dot
            val angle = Math.toRadians((startAngle + sweepAngle).toDouble())
            val dotX = centerX + radius * cos(angle).toFloat()
            val dotY = centerY + radius * sin(angle).toFloat()
            canvas.drawCircle(dotX, dotY, 12f, dotPaint)
        }

        // Draw percentage text
        val percentage = ((animatedProgress / maxProgress) * 100).toInt()
        val text = "$percentage%"

        // Draw text shadow
        textPaint.color = shadowColor
        canvas.drawText(text, centerX + 2f, centerY + 2f + textPaint.textSize / 3, textPaint)

        // Draw main text
        textPaint.color = textColor
        canvas.drawText( text,centerX, centerY + textPaint.textSize / 3, textPaint)

        // Draw subtitle
        textPaint.textSize = 24f
        textPaint.color = Color.parseColor("#888888")
        canvas.drawText( "",centerX, centerY + 60f, textPaint)
        textPaint.textSize = 48f // Reset text size
    }

    fun setProgress(progress: Float, animate: Boolean = true) {
        this.progress = progress.coerceIn(0f, maxProgress)

        if (animate) {
            animator?.cancel()
            animator = ValueAnimator.ofFloat(animatedProgress, this.progress).apply {
                duration = 1000
                interpolator = DecelerateInterpolator()
                addUpdateListener { animation ->
                    animatedProgress = animation.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            animatedProgress = this.progress
            invalidate()
        }
    }

    fun setMaxProgress(maxProgress: Float) {
        this.maxProgress = maxProgress
        invalidate()
    }

    fun getProgress(): Float = progress

    fun setStrokeWidth(width: Float) {
        this.strokeWidth = width
        setupPaints()
        invalidate()
    }

    // Pulse animation for special effects
    fun startPulseAnimation() {
        val pulseAnimator = ValueAnimator.ofFloat(1f, 1.2f, 1f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                scaleX = scale
                scaleY = scale
                alpha = 1.1f - (scale - 1f) * 2f
            }
        }
        pulseAnimator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}