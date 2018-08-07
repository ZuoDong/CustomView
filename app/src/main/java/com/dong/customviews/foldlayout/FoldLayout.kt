package com.dong.customviews.foldlayout

import android.content.Context
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * 作者：zuo
 * 时间：2018/7/3 11:29
 */
class FoldLayout:ViewGroup{

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private val mPaint = Paint()
    private val solidPaint = Paint()
    private val shadowPaint = Paint()
    private val mNumOfFolds = 8
    private val mMatrixs = ArrayList<Matrix>(mNumOfFolds)

    private fun init() {
        (0 until mNumOfFolds).forEach { mMatrixs.add(Matrix()) }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val child = getChildAt(0)
        measureChild(child,widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(child.measuredWidth,child.measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val child = getChildAt(0)
        child.layout(0,0,child.measuredWidth,child.measuredHeight)
    }
}