package com.oufenghua.homeworkchap4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

typealias TimeCallBack = (calendar: Calendar) -> Unit

class ClockView : View {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var backgroundPaintColor = Color.BLACK
    private var foregroundPaintColor = Color.WHITE

    companion object {
        private val FULL_DEGREE = 360f
        private val BOLD_PRE_COUNT = 5
        private val DEGREE_OFFSET: Float = (FULL_DEGREE / 60f)
        private val SECONDHAND_WIDTH = 4f

        private val SECOND = 1000f
        private val MINUTE = 60
        private val HOUR = 60
        private val DAY_HALF = 12
    }

    private var measureWidh = 0
    private var measureHeight = 0
    private var length: Float = 0f
    private val radius: Float
        get() {
            return length / 2
        }
    /**
     * 半径分 N 份
     */
    private val partitionCount = 100f
    /**
     * 每一份的长度
     */
    private val lengthPrePartition: Float
        get() {
            return radius / partitionCount
        }
    private val boldScaleLength: Float
        get() {
            return 10 * lengthPrePartition
        }
    private val normalScaleLength: Float
        get() {
            return 5 * lengthPrePartition
        }
    private val secondhandLength: Float
        get() {
            return 80 * lengthPrePartition
        }
    private val minutehandLength: Float
        get() {
            return 80 * lengthPrePartition
        }
    private val hourhandLength: Float
        get() {
            return 40 * lengthPrePartition
        }

    private var backgroundPaint: Paint
    private var foregroundPaint: Paint
    private var handPaint: Paint
    private var basePaint: Paint
    private val textSize: Float
        get() {
            return 10 * lengthPrePartition
        }
    private val boldScaleWidth: Float
        get() {
            return 3 * lengthPrePartition
        }
    private val normalScaleWidth: Float
        get() {
            return 1 * lengthPrePartition
        }
    var mTimeCallBack: TimeCallBack? = null

    init {
        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        handPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        basePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        initPaint()
    }

    fun initPaint() {
        backgroundPaint.color = backgroundPaintColor
        backgroundPaint.strokeWidth = normalScaleWidth
        backgroundPaint.style = Paint.Style.FILL

        foregroundPaint.color = foregroundPaintColor
        foregroundPaint.strokeWidth = boldScaleWidth
        foregroundPaint.style = Paint.Style.FILL
        foregroundPaint.textSize = textSize

        handPaint.color = foregroundPaintColor
        handPaint.strokeWidth = SECONDHAND_WIDTH
        handPaint.style = Paint.Style.FILL

        basePaint.color = Color.RED
        basePaint.strokeWidth = boldScaleWidth
        basePaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureWidh = MeasureSpec.getSize(widthMeasureSpec)
        measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        length = if (measureWidh > measureHeight) {
            measureHeight.toFloat()
        } else {
            measureWidh.toFloat()
        }
        // 重设参数
        initPaint()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.also {
            canvas.translate(measureWidh / 2f, measureHeight / 2f)
            canvas.save()
            drawBackground(it)
            canvas.restore()
            canvas.rotate(180f)
            basePaint.color = Color.RED
            drawHands(canvas)
            canvas.drawCircle(0f, 0f, 3 * lengthPrePartition, foregroundPaint)
        }

    }

    /**
     * 刻画表盘
     */
    private fun drawBackground(canvas: Canvas) {
        canvas.rotate(FULL_DEGREE / 2)
        canvas.drawCircle(0f, 0f, radius, backgroundPaint)
        var degree = DEGREE_OFFSET
        while (degree <= FULL_DEGREE) {
            canvas.rotate(DEGREE_OFFSET)
            if ((degree % BOLD_PRE_COUNT) == 0f) {
                // 每五格一次加粗加长
                var timeNumStr = ((degree / DEGREE_OFFSET).toInt() / BOLD_PRE_COUNT).toString()
                foregroundPaint.strokeWidth = boldScaleWidth
                canvas.drawLine(0f, radius - boldScaleLength, 0f, radius, foregroundPaint)
                var textlength = foregroundPaint.measureText(timeNumStr)
                canvas.drawText(timeNumStr, -(textlength / 2), radius - boldScaleLength - foregroundPaint.textSize, foregroundPaint)
            } else {
                foregroundPaint.strokeWidth = normalScaleWidth
                canvas.drawLine(0f, radius - normalScaleLength, 0f, radius, foregroundPaint)
            }
            degree += DEGREE_OFFSET
        }
        postInvalidateDelayed(16)
    }

    private fun drawHands(canvas: Canvas) {
        var date = Calendar.getInstance()
        val millSecond = date[Calendar.MILLISECOND]
        val second = date[Calendar.SECOND].toFloat()
        val minute = date[Calendar.MINUTE].toFloat()
        val hour = date[Calendar.HOUR].toFloat() % 12

        //  画时针分针
        handPaint.strokeWidth = boldScaleWidth
        handPaint.color = Color.WHITE
        drawHand(((minute + (second / MINUTE)) / HOUR) * FULL_DEGREE, minutehandLength, canvas)
        drawHand(((hour + (minute / HOUR)) / DAY_HALF) * FULL_DEGREE, hourhandLength, canvas)

        //  画秒针
        handPaint.strokeWidth = normalScaleWidth
        handPaint.color = Color.RED
        drawHand(((second + (millSecond / SECOND)) / MINUTE) * FULL_DEGREE, secondhandLength, canvas)

        mTimeCallBack?.also {
            it.invoke(date)
        }
    }

    private fun drawHand(degree: Float = 0f, handLength: Float = 0f, canvas: Canvas) {
        canvas.rotate(degree)
        canvas.drawLine(0f, 0f, 0f, handLength, handPaint)
        canvas.rotate(-degree)
    }

}