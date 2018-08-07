package com.dong.customviews.flowlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dong.customviews.R

/**
 * 作者：zuo
 * 时间：2018/6/1 14:27
 *
 * 标签布局
 * 支持padding
 * 支持item的左右间距horizontalSpace，上下间距verticalSpace
 * 暂不支持点击
 */
class FlowLayout:ViewGroup{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var verticalSpace = 0
    private var horizontalSpace = 0

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context,attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(MarginLayoutParams.MATCH_PARENT,MarginLayoutParams.MATCH_PARENT)
    }

    fun setVerticalSpace(verticalSpace: Int) {
        this.verticalSpace = verticalSpace
    }

    fun setHorizontalSpace(horizontalSpace: Int) {
        this.horizontalSpace = horizontalSpace
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
                width = Math.max(lineWidth - horizontalSpace,childWidth)
                height += lineHeight + verticalSpace

                lineWidth = childWidth + horizontalSpace
                lineHeight = childHeight
            }else{
                lineWidth += childWidth + horizontalSpace
                lineHeight = Math.max(lineHeight,childHeight)
            }

            if(index == childCount - 1){
                width = Math.max(width,lineWidth - horizontalSpace)
                height += lineHeight
            }
        }

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        setMeasuredDimension(if(widthMode == MeasureSpec.EXACTLY) widthSize else width,if(heightMode == MeasureSpec.EXACTLY) heightSize else height)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val flowWidth = measuredWidth - paddingRight

        var lineWidth = 0
        var lineHeight = 0
        var left = paddingLeft
        var top = paddingTop

        (0 until childCount).forEach { index ->
            val child = getChildAt(index)
            val params = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            val childHeight = child.measuredHeight + params.topMargin + params.bottomMargin

            if(lineWidth + childWidth > flowWidth){
                //如果换行,当前控件将跑到下一行，从最左边开始，所以left就是0，而top则需要加上上一行的行高，才是这个控件的top点;
                left = paddingLeft
                top += lineHeight + verticalSpace
                //同样，重新初始化lineHeight和lineWidth
                lineWidth = childWidth + horizontalSpace
                lineHeight = childHeight
            }else{
                lineWidth += childWidth + horizontalSpace
                lineHeight = Math.max(lineHeight,childHeight)
            }
            //计算childView的left,top,right,bottom
            val lc = left + params.leftMargin
            val tc = top + params.topMargin
            val rc = lc + child.measuredWidth
            val bc = tc + child.measuredHeight

            child.layout(lc,tc,rc,bc)
            //将left置为下一子控件的起始点
            left += childWidth + horizontalSpace
        }
    }


    fun addTags(datas:List<String>){
        for(str in datas){
            val item = View.inflate(context, R.layout.expand_flow_tags_item,null)
            val tv = item.findViewById<TextView>(R.id.appraise_tag_item)
            tv.text = str
            addView(item)
        }
    }
}