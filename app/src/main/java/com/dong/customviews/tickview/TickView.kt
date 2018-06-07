package com.dong.customviews.tickview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.graphics.PorterDuffXfermode
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator


/**
 * 作者：zuo
 * 时间：2018/6/5 17:48
 */
class TickView: View {
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initPaint()
        initAnim()
        initListener()
    }

    private val TAG = "TickView"
    private val mPaint = Paint()
    private val mCirclePaint = Paint()
    private val defaultWidth = 150
    private val defaultHeight = 150
    private var mStrokRadius = 70F
    private var mFillRadius = 75F
    private var cX = 75F
    private var cY = 75F
    private val mPath = Path()
    private val strokeWidth = 10F
    private var isCheckedState: Boolean = false
    private var offSetAngle: Int = -1
    private var arcRect:RectF? = null
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private var dRadius: Int = -1
    private var isShowTick = false
    private lateinit var ringAnim:ValueAnimator
    private lateinit var circleAnim:ValueAnimator
    private lateinit var animatorSet:AnimatorSet
    private var mCheckChangeListener: OnCheckChangeListener? = null

    private fun initPaint() {
        setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        mPaint.isAntiAlias = true
        mPaint.color = Color.GRAY
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = strokeWidth
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.pathEffect = CornerPathEffect(strokeWidth / 2)

        mCirclePaint.isAntiAlias = true
        mCirclePaint.color = Color.YELLOW
        mCirclePaint.style = Paint.Style.FILL
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(if(widthMode == MeasureSpec.EXACTLY) widthSize else defaultWidth,if(heightMode == MeasureSpec.EXACTLY) heightSize else defaultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mFillRadius = if(measuredWidth > measuredHeight){
            measuredHeight.toFloat() / 2
        }else{
            measuredWidth.toFloat() / 2
        }
        mStrokRadius = mFillRadius - strokeWidth / 2
        cX = mFillRadius
        cY = mFillRadius
        generateTickPath()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(arcRect == null){
            arcRect = RectF(strokeWidth / 2,strokeWidth / 2,mStrokRadius * 2 + strokeWidth / 2,mStrokRadius * 2 + strokeWidth / 2)
        }
        if(isCheckedState){
            mPaint.color = Color.GREEN
            if(offSetAngle != -1){
                //绘制圆弧
                mPaint.style = Paint.Style.STROKE
                canvas.drawArc(arcRect,90f,offSetAngle.toFloat(),false,mPaint)
            }

            if(dRadius != -1){
                //xfermode ， 禁用硬件加速，使用离屏绘制
                //绘制收缩圆环
                val layerId = canvas.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
                mPaint.style = Paint.Style.FILL
                canvas.drawCircle(cX,cY, mFillRadius,mPaint)
                mCirclePaint.xfermode = xfermode
                canvas.drawCircle(cX,cY,dRadius.toFloat(),mCirclePaint)
                mCirclePaint.xfermode = null
                canvas.restoreToCount(layerId)
            }

            if(isShowTick){
                //绘制对号
                mPaint.color = Color.WHITE
                mPaint.style = Paint.Style.STROKE
                canvas.drawPath(mPath,mPaint)
            }
        }else{
            //绘制默认图像
            mPaint.color = Color.GRAY
            mPaint.style = Paint.Style.STROKE
            canvas.drawCircle(cX,cY, mStrokRadius,mPaint)
            canvas.drawPath(mPath,mPaint)
        }
    }

    //生成对号的Path
    private fun generateTickPath() {
        mPath.moveTo(5F/7 * cX,cY)
        mPath.lineTo(13F/14 * cX,7F/6 * cY)
        mPath.lineTo(4F/3 * cX,3F/4 * cY)
    }

    private fun initAnim(){
        //画圆环动画
        ringAnim = ValueAnimator.ofInt(0, 360)
        ringAnim.duration = 500
        ringAnim.interpolator = LinearInterpolator()
        ringAnim.addUpdateListener { animation ->
            offSetAngle = animation.animatedValue as Int
            postInvalidate()
        }
        //圆圈缩小动画
        circleAnim = ValueAnimator.ofInt(mFillRadius.toInt(),0)
        circleAnim.duration = 300
        circleAnim.interpolator = DecelerateInterpolator()
        circleAnim.addUpdateListener {
            dRadius = it.animatedValue as Int
            postInvalidate()
        }

        //缩放动画
        val scaleXAnim = ObjectAnimator.ofFloat(this@TickView, "scaleX", 1f, 1.2f, 1f)
        val scaleYAnim = ObjectAnimator.ofFloat(this@TickView, "scaleY", 1f, 1.2f, 1f)
        animatorSet = AnimatorSet()
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.duration = 450
        animatorSet.playTogether(scaleXAnim,scaleYAnim)

        ringAnim.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
                Log.i(TAG,"ringAnim onAnimationRepeat")
            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.i(TAG,"ringAnim onAnimationEnd")
                offSetAngle = -1
                circleAnim.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
                Log.i(TAG,"ringAnim onAnimationCancel")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.i(TAG,"ringAnim onAnimationStart")
            }

        })
        circleAnim.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
                Log.i(TAG,"circleAnim onAnimationRepeat")
            }

            override fun onAnimationEnd(animation: Animator?) {
                isShowTick = true
                animatorSet.start()
                Log.i(TAG,"circleAnim onAnimationEnd")
            }

            override fun onAnimationCancel(animation: Animator?) {
                Log.i(TAG,"circleAnim onAnimationCancel")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.i(TAG,"circleAnim onAnimationStart")
            }

        })
    }

    private fun isAnimRunning() = ringAnim.isRunning || circleAnim.isRunning || animatorSet.isRunning

    private fun initListener(){
        setOnClickListener {
            updateCheckState(!isCheckedState)
        }
    }

    private fun updateCheckState(isChecked:Boolean){
        if(!isAnimRunning()){
            isCheckedState = isChecked
            if(isCheckedState){
                ringAnim.start()
            }else{
                dRadius = -1
                isShowTick = false
                postInvalidate()
            }
            mCheckChangeListener?.checkChanged(isCheckedState)
        }
    }

    fun getChecked():Boolean = isCheckedState

    //更改选中状态
    fun setChecked(isChecked:Boolean){
        updateCheckState(isChecked)
    }

    //设置监听
    fun setOnCheckChangeListener(listener: OnCheckChangeListener){
        mCheckChangeListener = listener
    }

    interface OnCheckChangeListener {
        fun checkChanged(isChecked:Boolean)
    }
}