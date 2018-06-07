package com.dong.customviews.flowlayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * 作者：zuo
 * 时间：2018/6/1 14:27
 */
class FlowLayout:ViewGroup{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context,attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(MarginLayoutParams.MATCH_PARENT,MarginLayoutParams.MATCH_PARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = 0
        var height = 0
        var lineWidth = 0
        var lineHeight = 0

        (0 until childCount).forEach { index ->
            val child = getChildAt(index)
            measureChild(child,widthMeasureSpec,heightMeasureSpec)
            val params = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            val childHeight = child.measuredHeight + params.topMargin + params.bottomMargin

            if(lineWidth + childWidth > widthSize){
                width = Math.max(lineWidth,childWidth)
                height += lineHeight

                lineWidth = childWidth
                lineHeight = childHeight
            }else{
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight,childHeight)
            }

            if(index == childCount - 1){
                width = Math.max(width,lineWidth)
                height += lineHeight
            }

        }

        setMeasuredDimension(if(widthMode == MeasureSpec.EXACTLY) widthSize else width,if(heightMode == MeasureSpec.EXACTLY) heightSize else height)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var lineWidth = 0
        var lineHeight = 0
        var left = 0
        var top = 0

        (0 until childCount).forEach { index ->
            val child = getChildAt(index)
            val params = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            val childHeight = child.measuredHeight + params.topMargin + params.bottomMargin

            if(lineWidth + childWidth > measuredWidth){
                //如果换行,当前控件将跑到下一行，从最左边开始，所以left就是0，而top则需要加上上一行的行高，才是这个控件的top点;
                left = 0
                top += lineHeight
                //同样，重新初始化lineHeight和lineWidth
                lineWidth = childWidth
                lineHeight = childHeight
            }else{
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight,childHeight)
            }
            //计算childView的left,top,right,bottom
            val lc = left + params.leftMargin
            val tc = top + params.topMargin
            val rc = lc + child.measuredWidth
            val bc = tc + child.measuredHeight

            child.layout(lc,tc,rc,bc)
            //将left置为下一子控件的起始点
            left += childWidth
        }
    }


}