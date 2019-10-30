package com.example.mapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mapp.R
import com.example.mapp.models.DiscoveryListViewRow
import java.security.CodeSource

class DiscoveryListViewAdapter(val context: Context, val source: ArrayList<DiscoveryListViewRow>): BaseAdapter() {
    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return source.size
    }

    override fun getItem(p0: Int): Any {
        return source[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.discovery_listview_row, p2, false)

        val mapNameTextView = rowView.findViewById<TextView>(R.id.map_name)
        val mapLocationTextView = rowView.findViewById<TextView>(R.id.map_location)
        val creatorTextView = rowView.findViewById<TextView>(R.id.creator)

        val discoveryListViewRow = getItem(p0) as DiscoveryListViewRow

        mapNameTextView.text = discoveryListViewRow.mapName
        mapLocationTextView.text = discoveryListViewRow.locationTag
        creatorTextView.text = discoveryListViewRow.creator
        Log.d("DiscoverListViewAdapter", "get view")
        return rowView
    }
}