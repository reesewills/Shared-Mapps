package com.example.mapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import com.example.mapp.adapters.DiscoveryListViewAdapter
import com.example.mapp.models.DiscoveryListViewRow
import com.example.mapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_mapps_new.*
import kotlinx.android.synthetic.main.fragment_my_mapps.*

class MyMappsActivity : AppCompatActivity() {
    var rowData = ArrayList<DiscoveryListViewRow>()
    lateinit var listView: ListView
    lateinit var adapter: DiscoveryListViewAdapter
    var db = FirebaseFirestore.getInstance()
    var mAuth = FirebaseAuth.getInstance()
    var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_mapps_new)
        this.listView = findViewById(R.id.my_mapps_list_view) as ListView
        adapter = DiscoveryListViewAdapter(this,rowData)
        listView.adapter = adapter

        db.collection("users").whereEqualTo("email", mAuth.currentUser?.email).get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    user.username = doc.get("username") as String
                }
                Log.d("MyMapps", "Got username: " + user.username)
                getMyMappsForAdapter()
            }

        //passes map id to the mapDisplay activity when a list view row is clicked
        listView.setOnItemClickListener {adapterView, view, i, l ->
            Log.d("MyMapps", i.toString()+" position clicked")
            val intent = Intent(this ,MapDisplay::class.java)
            intent.putExtra("mapID", rowData[i].mapID)
            intent.putExtra("activityName", "MyMappsActivity")
            startActivity(intent)

        }

        //long click to open visibility and delelte settings
        listView.setOnItemLongClickListener { adapterView, view, i, l ->
            val intent = Intent(this ,VisibilityDeleteMap::class.java)
            intent.putExtra("mapID", rowData[i].mapID)
            intent.putExtra("mapName",rowData[i].mapName)
            startActivity(intent)

            true
        }


        //create mapps button

        new_mapp.setOnClickListener {
            adapter.notifyDataSetChanged()
            val intent = Intent(this, MapBuilderTextStep::class.java)
            startActivity(intent)
        }

//        val fm = supportFragmentManager
//
//        // add
//        val ft = fm.beginTransaction()
//        println("PRINTING FRAG PLACEHOLDER")
//
//        //println(fm.findFragmentById(R.id.frag_placeholder)!!)
//        println("STOP")
//        ft.replace(R.id.frag_placeholder2, MyMappsFragment(this@MyMappsActivity))
//        //ft.remove(fm.findFragmentById(R.id.frag_placeholder)!!)
//        //ft.add(R.id.frag_placeholder, LeaderboardFragment(this@MainActivity), "LEADERBOARD_FRAG")
//        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        getMyMappsForAdapter()

    }

    private fun getMyMappsForAdapter() {
        Log.d("myMapps", "getMyMappsForAdapter Called")
        db.collection("maps").whereEqualTo("creatorName", user.username).get()
            .addOnSuccessListener { documents ->
                rowData.clear()
                for(map in documents) {
                    var mapObj = map.toObject(com.example.mapp.models.Map::class.java)

                        var rowDataItem = DiscoveryListViewRow()
                        rowDataItem.mapName = mapObj.name
                        rowDataItem.locationTag = mapObj.locationTag
                        rowDataItem.creator = mapObj.creatorName
                        rowDataItem.mapID = map.id



                        rowData.add(rowDataItem)




                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("MyMapps", "Error: getting maps", exception)
            }
    }
}
