package com.dong.customviews.LeafLoading

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.dong.customviews.R
import com.dong.customviews.utils.dp2px
import java.util.*
import kotlin.collections.ArrayList

/**
 * 作者：zuo
 * 时间：2018/6/19 16:52
 */
class LeafLoadingView: View {
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initPaint()
        initBitmap()
        startAnim()
        genertateLeafs()
    }

    private val TAG:String = "LeafLoadingView"
    private val DEFAULT_WIDTH by lazy { context.dp2px(300f) }
    private val DEFAULT_HEIGHT by lazy { context.dp2px(60f) }
    private val PADDING_FENG = 10
    private val bg_main_color = 0xFFFCE6A3.toInt()
    private val bg_ring_color = 0xFFFFFFFF.toInt()
    private val bg_circle_color = 0xFFFDCF50.toInt()
    private val progress_color = 0xFFFFA800.toInt()

    private val bgRectF by lazy { RectF(0f,0f,width.toFloat(),height.toFloat()) }
    private lateinit var leftRectF:RectF
    private lateinit var progressRectF:RectF

    private val mBgPaint = Paint()
    private val mProgressPaint = Paint()
    private lateinit var leafBitmap:Bitmap
    private lateinit var fengshanBitmap:Bitmap
    private var mRadius = 0f
    private var borderWidth = 10f
    private lateinit var fengRect:Rect
    private val fengMatrix:Matrix = Matrix() //风扇矩阵，平移，旋转
    private var totalValue:Int = 0
    private var currentProgress:Int = 40
    private var currentValue:Int = 0
    private var dRotate:Int = 0
    private lateinit var anim:ValueAnimator
    private var px = 0f //进度条左边圆x坐标
    private var py = 0f //进度条左边圆y坐标
    private var mPath = Path()
    private var progressRadius = 0
    private val random = Random()


    private fun initPaint(){
        mBgPaint.isAntiAlias = true
        mProgressPaint.isAntiAlias = true
        mProgressPaint.color = progress_color
        mProgressPaint.style = Paint.Style.FILL
    }

    private fun initBitmap() {
        leafBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.leaf)
        fengshanBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.fengshan)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(if(widthMode == MeasureSpec.EXACTLY) widthSize else DEFAULT_WIDTH.toInt(),
                if(heightMode == MeasureSpec.EXACTLY) heightSize else DEFAULT_HEIGHT.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = Math.min(w,h).toFloat() / 2
        totalValue = (w - borderWidth - mRadius).toInt()
        progressRadius = (mRadius - borderWidth).toInt()
        px = width - mRadius
        py = mRadius

        //指定Bitmap宽高
        fengshanBitmap = ThumbnailUtils.extractThumbnail(fengshanBitmap, ((mRadius - borderWidth - PADDING_FENG) * 2).toInt(), ((mRadius - borderWidth - PADDING_FENG) * 2).toInt())
        fengRect = Rect((w - mRadius * 2 + borderWidth).toInt() + PADDING_FENG,
                borderWidth.toInt() + PADDING_FENG,
                (w - borderWidth).toInt() - PADDING_FENG,
                (mRadius * 2 - borderWidth).toInt() - PADDING_FENG)

        leftRectF = RectF(borderWidth,borderWidth,mRadius * 2 - borderWidth,mRadius * 2 - borderWidth)

        fengMatrix.postTranslate(fengRect.left.toFloat(), fengRect.top.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        mBgPaint.color = bg_main_color
        mBgPaint.style = Paint.Style.FILL
        canvas?.drawRoundRect(bgRectF,mRadius,mRadius,mBgPaint)

        drawLeafs(canvas)

        generateProgressPath()
        canvas?.drawPath(mPath,mProgressPaint)

        mBgPaint.color = bg_ring_color
        mBgPaint.style = Paint.Style.STROKE
        mBgPaint.strokeWidth = borderWidth
        canvas?.drawCircle(px,py,mRadius - borderWidth / 2,mBgPaint)

        mBgPaint.color = bg_circle_color
        mBgPaint.style = Paint.Style.FILL
        canvas?.drawCircle(px,py,mRadius - borderWidth,mBgPaint)

        fengMatrix.postRotate(Math.toDegrees(10.0).toFloat(),px , py)
        canvas?.drawBitmap(fengshanBitmap,fengMatrix,mBgPaint)
    }

    private fun drawLeafs(canvas: Canvas?) {
        val currentTime = System.currentTimeMillis()
        for(leaf in leafInfos){
            getLeafLocation(leaf,currentTime)
        }
    }

    private fun getLeafLocation(leaf: Leaf, currentTime: Long) {

    }

    private fun startAnim(){
        anim = ValueAnimator.ofInt(0, 90)
        anim.duration = 5000
        anim.repeatCount = ValueAnimator.INFINITE
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener { animation ->
            val rotate = animation.animatedValue as Int
            if(rotate != dRotate){
                dRotate = animation.animatedValue as Int
                postInvalidate()
            }
        }
//        anim.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if(anim.isRunning){
            anim.cancel()
        }
    }

    private fun generateProgressPath(){
        mPath.reset()
        currentValue = (currentProgress.toFloat() / 100 * totalValue).toInt()
        if(currentValue <= progressRadius){
            val angle = Math.toDegrees(Math.acos((progressRadius - currentValue).toDouble() / progressRadius))
            mPath.addArc(leftRectF, (180 - angle).toFloat(), (angle * 2).toFloat())
        }else{
            progressRectF = RectF(mRadius,borderWidth,currentValue + borderWidth,mRadius * 2 - borderWidth)
            mPath.addArc(leftRectF, 90F, 180F)
            mPath.addRect(progressRectF,Path.Direction.CCW)
        }
        mPath.close()
    }

    private val MAX_LEAFS = 8
    private val LEAF_FLOAT_TIME:Long = 0L
    private var mAddTime:Long = 0L
    private val leafInfos = ArrayList<Leaf>()

    private fun genertateLeafs(){
        (0..MAX_LEAFS).forEach {
            val leaf = Leaf()
            leaf.rotateAngle = random.nextInt(360)
            // 随机旋转方向（顺时针或逆时针）
            leaf.rotateDirection = random.nextInt(2)
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mAddTime += random.nextInt((LEAF_FLOAT_TIME * 2).toInt())
            leaf.startTime = System.currentTimeMillis() + mAddTime

            leafInfos.add(leaf)
        }
    }

    class Leaf{
        var x:Int = 0
        var y:Int = 0
        var startTime:Long = 0L
        var rotateAngle:Int = 0
        var rotateDirection = 0
    }
}