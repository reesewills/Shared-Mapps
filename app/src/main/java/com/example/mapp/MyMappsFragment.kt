package com.example.mapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.arch.lifecycle.Observer
import android.content.Intent
import android.widget.TextView
import com.example.mapp.models.Map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_mapps.*
import kotlinx.android.synthetic.main.map_item.view.*
import java.util.ArrayList

@SuppressLint("ValidFragment")
class MyMappsFragment(context: Context): Fragment() {
    private var adapter = MapAdapter()
    private var parentContext: Context = context
    lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: MapViewModel

    private var scoresList: ArrayList<Map> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_my_mapps, container, false)


    }

    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        var currentUser: FirebaseUser = mAuth.getCurrentUser()!!;

        if(currentUser != null){
            var name: String = currentUser.getEmail()!!
        }
        //println("CHECK THIS MESSAGE")
        my_maps_list.layoutManager = LinearLayoutManager(parentContext)
        (my_maps_list as RecyclerView).addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)





        val observer = Observer<ArrayList<Map>> {
            (my_maps_list as RecyclerView).adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return false//scoresList[p0].creatorID == scoresList[p1].creatorID
                }

                override fun getOldListSize(): Int {
                    return scoresList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return scoresList[p0] == scoresList[p1]
                }
            })
            result.dispatchUpdatesTo(adapter)
            scoresList = it ?: ArrayList()
        }

        viewModel.getSavedMaps().observe(this, observer)

        //create mapps button

        new_map.setOnClickListener {
            adapter.notifyDataSetChanged()
            val intent = Intent(context, MapBuilderTextStep::class.java)
            startActivity(intent)
        }


    }



    inner class MapAdapter: RecyclerView.Adapter<MapAdapter.ScoreViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ScoreViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.map_item, p0, false)
            return ScoreViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ScoreViewHolder, p1: Int) {
            val product = scoresList[p1]
            p0.mapName.text = "" + product.name
            p0.locationTag.text = "" + product.locationTag
            p0.row.setOnClickListener {
                val intent = Intent(this@MyMappsFragment.parentContext, VisibilityDeleteMap::class.java)
                intent.putExtra("Name", product.name)

                //find some way of passing map id
                //intent.putExtra("someMapId", product.id)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return scoresList.size
        }

        inner class ScoreViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var row = itemView

            var mapName: TextView = itemView.map_name
            var locationTag: TextView = itemView.location_tag
        }
    }
}