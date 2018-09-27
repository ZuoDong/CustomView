package com.dong.customviews

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dong.customviews.LeafLoading.LeafLoadingActivity
import com.dong.customviews.cameratansform.CameraTransActivity
import com.dong.customviews.cameratansform.Rotate3dAnimation
import com.dong.customviews.drawtext.DrawTextActivity
import com.dong.customviews.flowlayout.FlowLayoutActivity
import com.dong.customviews.foldlayout.FoldLayoutActivity
import com.dong.customviews.pathmeasure.PathMeasureActivity
import com.dong.customviews.polygonimageview.PolygonActivity
import com.dong.customviews.redpoint.RedPointActivity
import com.dong.customviews.tickview.TickActivity
import com.dong.customviews.xformode.XforModeActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var datas = ArrayList<ItemBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        datas.add(ItemBean("Xformode", XforModeActivity::class.java))
        datas.add(ItemBean("拖动红点",RedPointActivity::class.java))
        datas.add(ItemBean("标签布局",FlowLayoutActivity::class.java))
        datas.add(ItemBean("TickView",TickActivity::class.java))
        datas.add(ItemBean("多边形",PolygonActivity::class.java))
        datas.add(ItemBean("叶子进度条",LeafLoadingActivity::class.java))
        datas.add(ItemBean("PathMeasure",PathMeasureActivity::class.java))
        datas.add(ItemBean("折叠窗",FoldLayoutActivity::class.java))
        datas.add(ItemBean("Camera3D旋转",CameraTransActivity::class.java))
        datas.add(ItemBean("绘制文字",DrawTextActivity::class.java))

        list_view.adapter = object :BaseAdapter(){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val textView = TextView(this@MainActivity)
                textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                textView.setPadding(0,40,0,40)
                textView.gravity = Gravity.CENTER
                textView.text = datas[position].str
                return textView
            }

            override fun getItem(position: Int): Any {
                return datas[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return datas.size
            }

        }
        list_view.setOnItemClickListener { parent, view, position, id ->
            startActivity(Intent(this@MainActivity,datas[position].clazz))
        }
    }
}
