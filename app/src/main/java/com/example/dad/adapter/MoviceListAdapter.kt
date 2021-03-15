package com.example.dad.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.dad.R
import com.example.dad.bean.Movie

class MoviceListAdapter(context: Context, sList: ArrayList<Movie>) : BaseAdapter() {
    private val mInflator: LayoutInflater = LayoutInflater.from(context)
    private var sList: ArrayList<Movie> = ArrayList();

    init {
        this.sList = sList;
    }

    override fun getCount(): Int {
        return sList.size
    }

    override fun getItem(position: Int): Any {
        return sList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val vh: ListRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.list_row, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }
        Glide.with(vh.img).load(sList[position].img).into(vh.img);
        return view
    }
}

private class ListRowHolder(row: View?) {
    val img: ImageView

    init {
        this.img = row?.findViewById(R.id.img_item) as ImageView
    }
}