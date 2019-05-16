package com.wdy.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.wdy.common.R
import com.wdy.common.utils.ResourceUtils

/**
 * 作者：RockQ on 2018/11/22
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */

class CircleWaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle), Runnable {
    private var mWidth: Float = 0.toFloat()
    private var mHeight: Float = 0.toFloat()

    private var centerX: Float = 0.toFloat() //圆心X
    private var centerY: Float = 0.toFloat() //圆心Y
    private var floatRadius: Float = 30.toFloat() //变化的半径
    private var maxRadius = -1f //圆半径
    @Volatile
    private var started = false
    private lateinit var mLinePaint: Paint
    private lateinit var mFillPaint: Paint
    private var waveColor = ResourceUtils.getColor(R.color.colorPrimary)
    private var centerAlign = true//居中
    private val bottomMargin = 0f//底部margin
    private var maxStrokeWith: Float = 0.0f
    private var minRadius: Float = 10f

    init {
        initView()
    }

    private fun initView() {
        mLinePaint = Paint()
        mFillPaint = Paint()
    }

    private fun init() {
        mWidth = width.toFloat()
        mHeight = height.toFloat()
        mLinePaint.isAntiAlias = true
        mLinePaint.strokeWidth = 2.0f
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.color = waveColor

        mFillPaint.isAntiAlias = true
        mFillPaint.strokeWidth = 0f
        mFillPaint.style = Paint.Style.STROKE
        mFillPaint.color = waveColor


        centerX = mWidth / 2.0f
        centerY = if (centerAlign) {
            mHeight / 2.0f
        } else {
            mHeight - bottomMargin
        }
        maxRadius = if (mWidth >= mHeight) {
            (mHeight / 2.0f + 2.0).toFloat()
        } else {
            (mHeight / 2.0f + 2.0).toFloat()
        }
        floatRadius = maxRadius * 0.382f
        minRadius = maxRadius * 0.382f
        maxStrokeWith = maxRadius - floatRadius
        start()
    }

    fun start() {
        if (!started) {
            started = true
            Thread(this).start()
        }
    }

    fun stop() {
        started = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (maxRadius <= 0.0f) {
            return
        }
        canvas.drawCircle(centerX, centerY, floatRadius, mLinePaint)
//        val alpha = 255 * (1 - floatRadius % maxRadius).toInt()
//        mFillPaint.alpha = alpha shr 2
//        val maxStrokeWidth = floatRadius - minRadius
//        mFillPaint.strokeWidth = maxStrokeWidth
//        canvas.drawCircle(centerX, centerY, floatRadius - maxStrokeWidth / 2, mFillPaint)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            init()
        } else {
            stop()
        }
    }

    override fun run() {
        while (started) {
            floatRadius += 4.0f
            if (floatRadius > maxRadius + 2) {
                floatRadius = maxRadius * 0.382f
            }
            postInvalidate()
            try {
                Thread.sleep(40L)
            } catch (localInterruptedException: InterruptedException) {
                localInterruptedException.printStackTrace()
            }

        }
    }

    fun setMaxRadius(maxRadius: Float) {
        this.maxRadius = maxRadius
    }

    fun setWaveColor(waveColor: Int) {
        this.waveColor = waveColor
    }

    fun setCenterAlign(centerAlign: Boolean) {
        this.centerAlign = centerAlign
    }


}