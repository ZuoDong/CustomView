package com.dong.customviews

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dong.customviews.xformode.XforModeActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var datas = ArrayList<ItemBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        datas.add(ItemBean("图片倒影", XforModeActivity::class.java))

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
