package com.dong.customviews.foldlayout

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ViewGroup



/**
 * 作者：zuo
 * 时间：2018/7/3 11:29
 *
 * 折叠窗
 */
open class FoldLayout:ViewGroup{

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private val solidPaint = Paint()
    private val shadowPaint = Paint()
    private val mNumOfFolds = 8 //折叠的个数
    private val mMatrixs = ArrayList<Matrix>(mNumOfFolds)
    private val mCanvas = Canvas()
    private val mBitmap:Bitmap by lazy { Bitmap.createBitmap(measuredWidth,measuredHeight,Bitmap.Config.ARGB_8888) }
    private var mFactor = 1.0f
    private var mTranslateDis = 0f
    private var mTranslateDisPerFold = 0f
    private var mFoldWidth = 0f
    private val shadowMatrix = Matrix()
    private lateinit var linearGradient:LinearGradient
    private val NUM_OF_POINT = 8 //4个点对应8个值
    private var isReady = false


    private fun init() {
        (0 until mNumOfFolds).forEach { mMatrixs.add(Matrix()) }
        shadowPaint.style = Paint.Style.FILL
        linearGradient = LinearGradient(0f, 0f, 0.5f, 0f, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP)
        shadowPaint.shader = linearGradient
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val child = getChildAt(0)
        measureChild(child,widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(child.measuredWidth,child.measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val child = getChildAt(0)
        child.layout(0,0,child.measuredWidth,child.measuredHeight)
        mCanvas.setBitmap(mBitmap)
        upDateFold()
    }

    private fun upDateFold() {
        mTranslateDis = measuredWidth * mFactor  //折叠后的宽度
        mFoldWidth = measuredWidth.toFloat() / mNumOfFolds
        mTranslateDisPerFold = mTranslateDis / mNumOfFolds

        val alpha = 255*(1-mFactor)
        solidPaint.color = Color.argb((alpha * 0.8f).toInt(),0,0,0) //折叠窗左边的阴影为全部

        shadowMatrix.setScale(mFoldWidth,1f)
        linearGradient.setLocalMatrix(shadowMatrix)
        shadowPaint.alpha = alpha.toInt()  //折叠窗右边的阴影为一半渐变。

        val depth:Float = (Math.sqrt((mFoldWidth * mFoldWidth - mTranslateDisPerFold * mTranslateDisPerFold).toDouble()) / 2).toFloat() //折叠高度


        //计算每个点的坐标
        val src = FloatArray(NUM_OF_POINT)
        val dst = FloatArray(NUM_OF_POINT)
        (0 until mNumOfFolds).forEach { i->
            mMatrixs[i].reset()

            src[0] = i * mFoldWidth
            src[1] = 0f
            src[2] = src[0] + mFoldWidth
            src[3] = 0f
            src[4] = src[2]
            src[5] = measuredHeight.toFloat()
            src[6] = src[0]
            src[7] = src[5]

            val isEven = i % 2 == 0

            dst[0] = i * mTranslateDisPerFold
            dst[1] = if (isEven) 0f else depth
            dst[2] = dst[0] + mTranslateDisPerFold
            dst[3] = if (isEven) depth else 0f
            dst[4] = dst[2]
            dst[5] = if (isEven) measuredHeight.toFloat() - depth else measuredHeight.toFloat()
            dst[6] = dst[0]
            dst[7] = if (isEven) measuredHeight.toFloat() else measuredHeight.toFloat() - depth

            for (y in 0 until NUM_OF_POINT) {
                dst[y] = Math.round(dst[y]).toFloat()
            }

            //图形形变，多以pointCount为4
            mMatrixs[i].setPolyToPoly(src,0,dst,0,4)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if(mFactor == 0f){
            return
        }
        if(mFactor == 1f){
            super.dispatchDraw(canvas)
            return
        }
        for (i in 0 until mNumOfFolds){
            canvas?.save()
            canvas?.concat(mMatrixs[i])
            canvas?.clipRect(mFoldWidth * i,0f,mFoldWidth * (i + 1),measuredHeight.toFloat())
            if(isReady){
                canvas?.drawBitmap(mBitmap,0f,0f,null)
            }else{
                super.dispatchDraw(mCanvas)
                canvas?.drawBitmap(mBitmap,0f,0f,null)
                isReady = true
            }
            canvas?.translate(mFoldWidth * i,0f)
            if(i % 2 == 0){
                canvas?.drawRect(0f,0f,mFoldWidth,height.toFloat(),solidPaint)
            }else{
                canvas?.drawRect(0f,0f,mFoldWidth,height.toFloat(),shadowPaint)
            }
            canvas?.restore()
        }
    }

    fun setFactor(factor:Float){
        mFactor = factor
        upDateFold()
        invalidate()
    }

    fun getFactor():Float = mFactor
}