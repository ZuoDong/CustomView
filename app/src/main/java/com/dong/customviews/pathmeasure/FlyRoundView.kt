package com.dong.customviews.pathmeasure

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.dong.customviews.R

/**
 * 作者：zuo
 * 时间：2018/6/27 16:35
 */
class FlyRoundView: View {
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private val TAG = "FlyRoundView"
    private val mPaint = Paint()
    private var mRadius:Float = 0f
    private var pointX:Float = 0f
    private var pointY:Float = 0f
    private val mPath = Path()
    private lateinit var mBitmap:Bitmap
    private val mMatrix = Matrix()
    private lateinit var mPathMeasure:PathMeasure
    private var currentValue = 0f
    private val pos:FloatArray = FloatArray(2)
    private val tan:FloatArray = FloatArray(2)


    private fun init() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 5f

        val options = BitmapFactory.Options()
        options.inSampleSize = 2
        mBitmap = BitmapFactory.decodeResource(resources, R.mipmap.arrow,options)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size,size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = w.toFloat() / 4
        pointX = mRadius * 2
        pointY = mRadius * 2

        mPath.addCircle(pointX,pointY,mRadius,Path.Direction.CW)
        mPathMeasure = PathMeasure(mPath,false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        currentValue += 0.005f
        if(currentValue > 1){
            currentValue = 0f
        }
        mPathMeasure.getPosTan(mPathMeasure.length * currentValue,pos,tan)
        val angle = Math.atan2(tan[1].toDouble(), tan[0].toDouble()) * 180 / Math.PI
//        Log.i(TAG,"angle = $angle")
        mMatrix.reset()
        mMatrix.postRotate(angle.toFloat(),mBitmap.width.toFloat() / 2,mBitmap.height.toFloat() / 2)
        mMatrix.postTranslate(pos[0] - mBitmap.width.toFloat() / 2,pos[1] - mBitmap.height.toFloat() / 2)

        canvas?.drawBitmap(mBitmap,mMatrix,mPaint)
        canvas?.drawPath(mPath,mPaint)

        //请使用ValueAnimator进行控制
        invalidate()
    }

}