package com.koniukhov.waterreminder.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.koniukhov.waterreminder.R
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin




class WaterProgressCircle(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var amplitudeRatio = 0f
    private var centerText: String? = null
    private var bottomText: String? = null
    private var defaultWaterLevel = 0f
    private var waveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO
    private var progress = 0
    private var waterLevelRatio = progress.toFloat() / HUNDRED_PERCENT
    private var wavesShader: BitmapShader? = null
    private val shaderMatrix = Matrix()
    private val foregroundWavePaint = Paint()
    private val backgroundWavePaint = Paint()
    private val backgroundPaint = Paint()
    private val bottomTextPaint = Paint()
    private val centerTextPaint = Paint()
    private var waveShiftAnim: ObjectAnimator? = null
    private var animatorSet: AnimatorSet? = null

    init {
        foregroundWavePaint.isAntiAlias = true
        backgroundWavePaint.isAntiAlias = true
        backgroundPaint.isAntiAlias = true
        centerTextPaint.isAntiAlias = true
        bottomTextPaint.isAntiAlias = true
        backgroundWavePaint.color = BACKGROUND_WAVE_COLOR
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.color = BACKGROUND_COLOR
        centerTextPaint.color = TEXT_COLOR
        centerTextPaint.style = Paint.Style.FILL
        bottomTextPaint.color = TEXT_COLOR
        bottomTextPaint.style = Paint.Style.FILL
        initAnimation()
        setProgress(progress)
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.WaterProgressCircle, 0, 0).apply {
            try {
                val amplitudeRatioAttr = getFloat(R.styleable.WaterProgressCircle_waveAmplitude, DEFAULT_AMPLITUDE_VALUE) / AMPLITUDE_DIVIDER
                amplitudeRatio = min(amplitudeRatioAttr, DEFAULT_AMPLITUDE_RATIO);
                progress = getInteger(R.styleable.WaterProgressCircle_progress, DEFAULT_PROGRESS)
                centerTextPaint.textSize = getDimension(R.styleable.WaterProgressCircle_textCenterSize, DEFAULT_TEXT_CENTER_SIZE)
                centerText = getString(R.styleable.WaterProgressCircle_textCenter)
                bottomTextPaint.textSize = getDimension(R.styleable.WaterProgressCircle_textBottomSize, DEFAULT_TEXT_BOTTOM_SIZE)
                bottomText = getString(R.styleable.WaterProgressCircle_textBottom)
            }finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (wavesShader != null) {
            if (foregroundWavePaint.shader == null) {
                foregroundWavePaint.shader = wavesShader
            }
            shaderMatrix.setScale(
                MATRIX_SCALE.toFloat(),
                amplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                0f,
                defaultWaterLevel
            )
            val dx = waveShiftRatio * width
            val dy = (DEFAULT_WATER_LEVEL_RATIO - waterLevelRatio) * height
            shaderMatrix.postTranslate(dx, dy)
            wavesShader?.setLocalMatrix(shaderMatrix)
            val radius = width / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, backgroundWavePaint)
            canvas.drawCircle(width / 2f, height / 2f, radius, foregroundWavePaint)
            if (centerText!!.isNotEmpty()) {
                val middle = centerTextPaint.measureText(centerText)
                canvas.drawText(
                    centerText!!,
                    (width - middle) / 2,
                    height / 2 - (centerTextPaint.descent() + centerTextPaint.ascent()) / 2,
                    centerTextPaint
                )
            }
            if (bottomText!!.isNotEmpty()) {
                val bottom = bottomTextPaint.measureText(bottomText)
                canvas.drawText(
                    bottomText!!,
                    (width - bottom) / 2,
                    height * 8 / 10.0f - (bottomTextPaint.descent() + bottomTextPaint.ascent()) / 2,
                    bottomTextPaint
                )
            }
        } else {
            foregroundWavePaint.shader = null
        }
    }

    fun setCenterText(text: String){
        centerText = text
        invalidate()
    }

    fun setBottomText(text: String){
        bottomText = text
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateWaveShader()
    }

    override fun onDetachedFromWindow() {
        cancelAnimation()
        super.onDetachedFromWindow()
    }

    private fun adjustAlpha(): Int {
        val alpha = (Color.alpha(FOREGROUND_WAVE_COLOR) * ALPHA_FACTOR).roundToInt()
        val red = Color.red(FOREGROUND_WAVE_COLOR)
        val green = Color.green(FOREGROUND_WAVE_COLOR)
        val blue = Color.blue(FOREGROUND_WAVE_COLOR)
        return Color.argb(alpha, red, green, blue)
    }

    fun setWaveShiftRatio(waveShiftRatio: Float) {
        if (this.waveShiftRatio != waveShiftRatio) {
            this.waveShiftRatio = waveShiftRatio
            invalidate()
        }
    }

    fun setWaterLevelRatio(waterLevelRatio: Float) {
        if (this.waterLevelRatio != waterLevelRatio) {
            this.waterLevelRatio = waterLevelRatio
            invalidate()
        }
    }

    private fun cancelAnimation() {
        if (animatorSet != null) {
            animatorSet?.cancel()
        }
    }

    private fun initAnimation() {
        waveShiftAnim = ObjectAnimator.ofFloat(
            this,
            WAVE_SHIFT_RATIO_NAME,
            WAVES_SHIFT_RATIO_FROM,
            WAVES_SHIFT_RATIO_TO
        )
        waveShiftAnim?.repeatCount = ValueAnimator.INFINITE
        waveShiftAnim?.duration = ANIMATION_DURATION.toLong()
        waveShiftAnim?.interpolator = LinearInterpolator()
        waveShiftAnim?.repeatCount = ANIMATION_REPEAT_COUNT
        animatorSet = AnimatorSet()
        animatorSet?.play(waveShiftAnim)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        val waterLevelAnim = ObjectAnimator.ofFloat(
            this, WATER_LEVEL_RATIO_NAME,
            waterLevelRatio,
            this.progress.toFloat() / HUNDRED_PERCENT
        )
        waterLevelAnim.duration = ANIMATION_DURATION.toLong()
        waterLevelAnim.interpolator = DecelerateInterpolator()
        waterLevelAnim.start()
        val animatorSetProgress = AnimatorSet()
        animatorSetProgress.play(waterLevelAnim)
        animatorSetProgress.start()
        animatorSet?.start()
    }

    private fun updateWaveShader() {
        val width = measuredWidth
        val height = measuredHeight
        if (width > 0 && height > 0) {
            val defaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width
            val defaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO
            defaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val wavePaint = Paint()
            wavePaint.strokeWidth = WAVE_STROKE_WIDTH.toFloat()
            wavePaint.isAntiAlias = true
            val endX = width + 1
            val endY = height + 1
            val waveY = FloatArray(endX)
            wavePaint.color = adjustAlpha()
            for (beginX in 0 until endX) {
                val wx = beginX * defaultAngularFrequency
                val beginY = (defaultWaterLevel + defaultAmplitude * sin(wx)).toFloat()
                canvas.drawLine(
                    beginX.toFloat(),
                    beginY,
                    beginX.toFloat(),
                    endY.toFloat(),
                    wavePaint
                )
                waveY[beginX] = beginY
            }
            wavePaint.color = FOREGROUND_WAVE_COLOR
            val wave2Shift = (width.toFloat() / 4).toInt()
            for (beginX in 0 until endX) {
                canvas.drawLine(
                    beginX.toFloat(),
                    waveY[(beginX + wave2Shift) % endX], beginX.toFloat(), endY.toFloat(), wavePaint
                )
            }
            wavesShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
            foregroundWavePaint.shader = wavesShader
        }
    }
    companion object{
        private const val DEFAULT_AMPLITUDE_RATIO = 0.1f
        private const val DEFAULT_AMPLITUDE_VALUE = 50.0f
        private const val DEFAULT_WATER_LEVEL_RATIO = 0.5f
        private const val DEFAULT_WAVE_LENGTH_RATIO = 1f
        private const val DEFAULT_WAVE_SHIFT_RATIO = 0.0f
        private const val AMPLITUDE_DIVIDER = 1000
        private const val DEFAULT_PROGRESS = 0
        private val FOREGROUND_WAVE_COLOR = Color.parseColor("#0153e7")
        private val BACKGROUND_WAVE_COLOR = Color.parseColor("#0276f1")
        private val BACKGROUND_COLOR = Color.parseColor("#258dfc")
        private const val TEXT_COLOR = Color.WHITE
        private const val DEFAULT_TEXT_CENTER_SIZE = 24.0f
        private const val DEFAULT_TEXT_BOTTOM_SIZE = 16.0f
        private const val ALPHA_FACTOR = 0.3f
        private const val WAVES_SHIFT_RATIO_FROM = 0f
        private const val WAVES_SHIFT_RATIO_TO = 1f
        private const val ANIMATION_DURATION = 2000
        private const val HUNDRED_PERCENT = 100
        private const val MATRIX_SCALE = 1
        private const val ANIMATION_REPEAT_COUNT = 1
        private const val WAVE_STROKE_WIDTH = 2
        private const val WAVE_SHIFT_RATIO_NAME = "waveShiftRatio"
        private const val WATER_LEVEL_RATIO_NAME = "waterLevelRatio"
    }
}