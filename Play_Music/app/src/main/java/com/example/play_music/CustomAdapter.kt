package com.example.play_music

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class CustomAdapter (internal var context: Context, arrayListDetails:ArrayList<Model>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private var arrayListDetails:ArrayList<Model>
    internal var contactListFiltered: List<Model> = ArrayList()

    init {
        this.layoutInflater = LayoutInflater.from(context)
        this.arrayListDetails=arrayListDetails
    }

    override fun getCount(): Int {
        return arrayListDetails.size
    }

    override fun getItem(position: Int): Any {
        return arrayListDetails.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = this.layoutInflater.inflate(R.layout.adapter_layout, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.tvTitle.text = arrayListDetails.get(position).title
        listRowHolder.tvLink.text = arrayListDetails.get(position).link
        listRowHolder.tvId.text = arrayListDetails.get(position).id
        listRowHolder.imgView.setOnClickListener { view->
            val album= arrayListDetails[position]
            val intent = Intent(context,  Play_Music::class.java)
            intent.putExtra("song", album.title)
            intent.putExtra("url", album.link)
            context.startActivity(intent)

        }
        return view
    }

    private class ListRowHolder(row: View?) {
        public val tvTitle: TextView
        public val tvLink: TextView
        public val tvId: TextView
        public val imgView: ImageView
        public val linearLayout: LinearLayout

        init {
            this.tvId = row?.findViewById<TextView>(R.id.tvId) as TextView
            this.tvTitle = row?.findViewById<TextView>(R.id.tvTitle) as TextView
            this.tvLink = row?.findViewById<TextView>(R.id.tvLink) as TextView
            this.imgView= row?.findViewById<ImageView>(R.id.img_play) as ImageView
            this.linearLayout = row?.findViewById<LinearLayout>(R.id.linearLayout) as LinearLayout

        }
    }
}
