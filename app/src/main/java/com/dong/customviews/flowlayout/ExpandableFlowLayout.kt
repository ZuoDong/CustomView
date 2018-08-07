package com.dong.customviews.flowlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dong.customviews.R

/**
 * 作者：zuo
 * 时间：2018/7/8 14:00
 *
 * 可伸展的flowLayout
 *
 * 大于指定行数则自动显示扩展布局
 * 不支持padding
 * 可设置item间距horizontalSpace
 * 行间距verticalSpace
 */
class ExpandableFlowLayout : ViewGroup {
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    private lateinit var clickView: View
    private var limitRows = 3  //显示的最小行数
    private var isExpand = false  //是否在展开状态
    private val TAG = "ExpandableFlowLayout"
    private lateinit var showAllTv: TextView
    private lateinit var showAllArrow: ImageView
    private val clickViewParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
    private var verticalSpace = 0
    private var horizontalSpace = 0

    private fun initView(){
        clickView = View.inflate(context, R.layout.expand_flowlayout_click_view,null)
        showAllTv = clickView.findViewById(R.id.show_all_tv)
        showAllArrow = clickView.findViewById(R.id.show_all_arrow)
        clickView.setOnClickListener{
            isExpand = !isExpand
            changeState(isExpand)
            requestLayout()
        }
    }

    fun setVerticalSpace(verticalSpace: Int) {
        this.verticalSpace = verticalSpace
    }

    fun setHorizontalSpace(horizontalSpace: Int) {
        this.horizontalSpace = horizontalSpace
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if(clickView.parent == null){
            addView(clickView,clickViewParams)
        }

//        Log.i(TAG,"$childCount")

        var width = 0
        var height = 0
        var lineWidth = 0
        var lineHeight = 0
        var row = 0
        val tagNum = childCount - 1

        if(tagNum > 0){ //如果tag数量大于0，则至少有一行
            row = 1
        }

        for(index in (0 until tagNum)){
            val child = getChildAt(index)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val params = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            val childHeight = child.measuredHeight + params.topMargin + params.bottomMargin

            if (lineWidth + childWidth > widthSize) {
                row++  //行数

                width = Math.max(lineWidth - horizontalSpace, childWidth)
                height += lineHeight

                lineWidth = childWidth + horizontalSpace
                lineHeight = childHeight

                if(!isExpand && row > limitRows){ //未展开并且行数大于限制行数 则跳出
//                    Log.i(TAG,"跳出去了")
                    break
                }else{
                    height += verticalSpace
                }
            } else {
                lineWidth += childWidth + horizontalSpace
                lineHeight = Math.max(lineHeight, childHeight)
            }

            if (index == tagNum - 1) {
                width = Math.max(width, lineWidth - horizontalSpace)
                height += lineHeight
            }

//            Log.i(TAG,"onMeasure row:$row  limitRows:$limitRows")
        }

        if(row > limitRows){
            val clickChild = getChildAt(childCount - 1)
            measureChild(clickChild, widthMeasureSpec, heightMeasureSpec)

            width = Math.max(width,clickChild.measuredWidth)
            height += clickChild.measuredHeight
        }

        setMeasuredDimension(if (widthMode == MeasureSpec.EXACTLY) widthSize else width, if (heightMode == MeasureSpec.EXACTLY) heightSize else height)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var lineWidth = 0
        var lineHeight = 0
        var left = 0
        var top = 0
        var row = 0
        val tagNum = childCount - 1
        var lastY = 0

        if(tagNum > 0){ //如果tag数量大于0，则至少有一行
            row = 1
        }

        for(index in (0 until tagNum)){
            val child = getChildAt(index)
            val params = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            val childHeight = child.measuredHeight + params.topMargin + params.bottomMargin

            if (lineWidth + childWidth > measuredWidth) {
                //如果换行,当前控件将跑到下一行，从最左边开始，所以left就是0，而top则需要加上上一行的行高，才是这个控件的top点;
                row++
                left = 0
                top += lineHeight + verticalSpace
                //同样，重新初始化lineHeight和lineWidth
                lineWidth = childWidth + horizontalSpace
                lineHeight = childHeight

                if(!isExpand && row > limitRows){  //未展开并且行数大于限制行数 则跳出
                    break
                }
            } else {
                lineWidth += childWidth + horizontalSpace
                lineHeight = Math.max(lineHeight, childHeight)
            }
            //计算childView的left,top,right,bottom
            val lc = left + params.leftMargin
            val tc = top + params.topMargin
            val rc = lc + child.measuredWidth
            val bc = tc + child.measuredHeight

            lastY = bc

            child.layout(lc, tc, rc, bc)

            //将left置为下一子控件的起始点
            left += childWidth + horizontalSpace
        }

        if(row > limitRows){
            val clickChild = getChildAt(childCount - 1)
            clickChild.layout(0,lastY,measuredWidth,lastY + clickChild.measuredHeight)
        }
    }

    private fun changeState(expand: Boolean) {
        showAllTv.text = if(expand) "全部收起" else "查看全部"
        showAllArrow.setImageResource(if(expand) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float)
    }

    fun addTags(datas:List<String>){
        for(str in datas){
            val item = View.inflate(context,R.layout.expand_flow_tags_item,null)
            val tv = item.findViewById<TextView>(R.id.appraise_tag_item)
            tv.text = str
            addView(item)
        }
    }
}