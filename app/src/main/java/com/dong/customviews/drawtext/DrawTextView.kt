package com.dong.customviews.drawtext

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * 作者：zuo
 * 时间：2018/9/17 10:25
 */
class DrawTextView:View{
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initPaint()
    }

    private val mPaint = Paint()
    private val phoneWidth by lazy { resources.displayMetrics.widthPixels.toFloat() }

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.textSize = 60f
    }

    override fun onDraw(canvas: Canvas?) {
        val baseLineX = 0f
        val baseLineY = 80f

        val metrics = mPaint.fontMetrics
        mPaint.color = Color.RED
        canvas?.drawLine(0f,baseLineY,phoneWidth,baseLineY,mPaint)

        mPaint.color = Color.GREEN
        canvas?.drawLine(0f,baseLineY + metrics.top,phoneWidth,baseLineY + metrics.top,mPaint)

        mPaint.color = Color.BLUE
        canvas?.drawLine(0f,baseLineY + metrics.ascent,phoneWidth,baseLineY + metrics.ascent,mPaint)

        mPaint.color = Color.BLUE
        canvas?.drawLine(0f,baseLineY + metrics.descent,phoneWidth,baseLineY + metrics.descent,mPaint)

        mPaint.color = Color.GREEN
        canvas?.drawLine(0f,baseLineY + metrics.bottom,phoneWidth,baseLineY + metrics.bottom,mPaint)

        mPaint.color = Color.BLACK
        canvas?.drawText("mPaint的fontMetrics",baseLineX,baseLineY,mPaint)


        val rectText = "paint MeasureText And getTextBound"
        val width = mPaint.measureText(rectText)
        val baseLineY2 = baseLineY * 2.5f
        mPaint.color = Color.YELLOW
        canvas?.drawRect(RectF(baseLineX,baseLineY2 + metrics.top,baseLineX + width,baseLineY2 + metrics.bottom),mPaint)

        val boundsRect = Rect()
        mPaint.getTextBounds(rectText,0,rectText.length,boundsRect)
        boundsRect.top = (baseLineY2 + boundsRect.top).toInt()
        boundsRect.bottom = (baseLineY2 + boundsRect.bottom).toInt()
        mPaint.color = Color.RED
        canvas?.drawRect(boundsRect,mPaint)

        mPaint.color = Color.BLACK
        canvas?.drawText(rectText,baseLineX,baseLineY2,mPaint)

        canvas?.save()
        canvas?.translate(0f,240f)
        val textPaint = TextPaint()
        textPaint.color = Color.BLACK
        textPaint.textSize = 60f
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        val layoutText = "BoringLayout 主要适合单行文本显示的情况\nDynamicLayout 主要特性是在支持多行的情况下最关键的特性是监听文本的改动，静态文本显示 不建议使用这个，性能略低StaticLayout \nStaticLayout 顾名思义特点适合多行静态文本显示的情况"
        val staticlayout = StaticLayout(layoutText,textPaint,canvas?.width?:phoneWidth.toInt(),Layout.Alignment.ALIGN_NORMAL,1.0f,0f,true)
        staticlayout.draw(canvas)
        canvas?.restore()
    }
}