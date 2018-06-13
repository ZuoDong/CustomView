package com.dong.customviews.polygonimageview

import android.content.Context
import android.graphics.*
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.widget.ImageView
import com.dong.customviews.R
import com.dong.customviews.utils.drawableToBitmap

/**
 * 作者：zuo
 * 时间：2018/6/11 15:07
 */
class PolygonImageView:ImageView{
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initAttrs(attrs,defStyleAttr)
        initPaint()
    }

    private val mPaint = Paint()
    private val mBorderPaint = Paint()
    private var strokWidth = 15F
    private var canvasWidth = 0
    private var canvasHeight = 0
    private var hasBorder: Boolean = false
    private var mDiameter = 0f
    private var centerX = 0f
    private var centerY = 0f
    private val mPath:Path = Path()
    private var vertexNum = 6
    private var rotateAngle:Double = 30.0
    private var cornerRadius:Float = 0f
    private var borderColor:Int =  Color.RED
    private val rectF:RectF = RectF()


    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val attributes = context.theme.obtainStyledAttributes(attrs,
                R.styleable.PolygonImageView, defStyleAttr, 0)
        try {
            rotateAngle = attributes.getFloat(R.styleable.PolygonImageView_poly_rotation_angle, 0f).toDouble()
            vertexNum = attributes.getInteger(R.styleable.PolygonImageView_poly_vertices, 6)
            cornerRadius = attributes.getFloat(R.styleable.PolygonImageView_poly_corner_radius, 0f)
            hasBorder = attributes.getBoolean(R.styleable.PolygonImageView_poly_border, false)
            borderColor = attributes.getColor(R.styleable.PolygonImageView_poly_border_color, Color.WHITE)
            strokWidth = attributes.getDimension(R.styleable.PolygonImageView_poly_border_width, 4f)
        } finally {
            attributes.recycle()
        }
    }

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.pathEffect = CornerPathEffect(cornerRadius)

        mBorderPaint.isAntiAlias = true
        mBorderPaint.pathEffect = CornerPathEffect(cornerRadius)
        mBorderPaint.alpha = 100
        mBorderPaint.style = Paint.Style.STROKE

        if(hasBorder){
            mBorderPaint.color = borderColor
            mBorderPaint.strokeWidth = strokWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measure(widthMeasureSpec)
        val height = measure(heightMeasureSpec)
        setMeasuredDimension(width,height)
    }

    private fun measure(spec:Int):Int{
        val mode = MeasureSpec.getMode(spec)
        val size = MeasureSpec.getSize(spec)
        return if(mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST){
            size
        }else{
            Math.min(canvasWidth,canvasHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //当宽高改变的时候，应该重新计算每个点的位置和中心店的坐标，以及刷新图片
        canvasWidth = w
        canvasHeight = h

        updatePolySize(paddingLeft,paddingTop,paddingTop,paddingBottom)

        if(Math.min(canvasWidth,canvasHeight) != Math.min(oldw,oldh)){
            refreshImage()
        }
    }

    private fun refreshImage() {
        if(drawable != null){
            val bitmap = drawable.drawableToBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
            val canvasSize = Math.min(canvasWidth,canvasHeight)
            mPaint.shader = BitmapShader(ThumbnailUtils.extractThumbnail(bitmap, canvasSize, canvasSize),Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        }
    }

    private fun updatePolySize(pl:Int, pt:Int, pr:Int, pb:Int){
        val borderPadding =  if(hasBorder) strokWidth else 0f
        val xPadding = pl + pr + borderPadding * 2
        val yPadding = pt + pb + borderPadding * 2
        val diameter = Math.min(canvasWidth - xPadding,canvasHeight - yPadding)
        if(diameter != mDiameter){
            mDiameter = diameter
            centerX = mDiameter / 2 + pl + strokWidth
            centerY = mDiameter / 2 + pt + strokWidth
            if(vertexNum < 3){
                return
            }
            getPolyPath()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if(drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0){
            return
        }
        when(vertexNum){
            0 -> {  //画圆
                canvas.drawCircle(centerX,centerY,mDiameter / 2,mPaint)
                if(hasBorder){
                    canvas.drawCircle(centerX,centerY,mDiameter / 2,mBorderPaint)
                }
            }
            1 -> {  //什么都不做
                super.onDraw(canvas)
            }
            2 -> {
                rectF.set(centerX - mDiameter / 2, centerY - mDiameter / 2, centerX + mDiameter / 2, centerY + mDiameter / 2)
                canvas.drawRoundRect(rectF,cornerRadius,cornerRadius,mPaint)
                if(hasBorder){
                    canvas.drawRoundRect(rectF,cornerRadius,cornerRadius,mBorderPaint)
                }
            }
            else -> {  //画多边形
                canvas.drawPath(mPath,mPaint)
                if(hasBorder){
                    canvas.drawPath(mPath,mBorderPaint)
                }
            }
        }
    }

    private fun getPolyPath() {
        val angleRadians = Math.toRadians(rotateAngle)
        mPath.reset()
        var i = 0
        do {
            val pointX = centerX + mDiameter / 2 * Math.cos(2 * Math.PI * i / vertexNum)
            val pointY = centerY + mDiameter / 2 * Math.sin(2 * Math.PI * i / vertexNum)
            val nextVertexX = Math.cos(angleRadians) * (pointX - centerX) - Math.sin(angleRadians) * (pointY - centerY) + centerX
            val nextVertexY = Math.sin(angleRadians) * (pointX - centerX) + Math.cos(angleRadians) * (pointY - centerY) + centerY

            if(i == 0){
                mPath.moveTo(nextVertexX.toFloat(),nextVertexY.toFloat())
            }else{
                mPath.lineTo(nextVertexX.toFloat(),nextVertexY.toFloat())
            }
            i++
        }while (i <= vertexNum)
        mPath.close()
    }

}