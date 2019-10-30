package com.example.mapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import com.example.mapp.adapters.DiscoveryListViewAdapter
import com.example.mapp.models.DiscoveryListViewRow
import com.example.mapp.models.Map
import com.example.mapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class SavedMappsActivity : AppCompatActivity() {

    var rowData = ArrayList<DiscoveryListViewRow>()
    lateinit var listView: ListView
    lateinit var adapter: DiscoveryListViewAdapter
    var db = FirebaseFirestore.getInstance()
    var mAuth = FirebaseAuth.getInstance()
    var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followed_maps_new)

        this.listView = findViewById(R.id.followed_maps_list_view) as ListView
        adapter = DiscoveryListViewAdapter(this,rowData)
        listView.adapter = adapter


        //passes map id to the mapDisplay activity when a list view row is clicked
        listView.setOnItemClickListener {adapterView, view, i, l ->
            Log.d("Saved Mapps", i.toString()+" position clicked")
            val intent = Intent(this ,MapDisplay::class.java)
            intent.putExtra("mapID", rowData[i].mapID)
            intent.putExtra("activityName", "SavedMappsActivity")
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
//        ft.replace(R.id.frag_placeholder3, SavedMappsFragment(this@SavedMappsActivity))
//        //ft.remove(fm.findFragmentById(R.id.frag_placeholder)!!)
//        //ft.add(R.id.frag_placeholder, LeaderboardFragment(this@MainActivity), "LEADERBOARD_FRAG")
//        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        getFollowedMapsForAdapter()

    }

    private fun getFollowedMapsForAdapter() {
        Log.d("Saved Mapps", "getFollowedMapsForAdapter called")


        db.collection("users").whereEqualTo("email", mAuth.currentUser?.email).get()
            .addOnSuccessListener { documents ->
                rowData.clear()

                for(doc in documents) {

                    user = doc.toObject(User::class.java)

                }

                //query for all followed map ids and then add them to row data

                Log.d("Saved Mapps", user.username + " " + user.followedMaps.size.toString())

                for (mapId in user.followedMaps) {
                    db.collection("maps").whereEqualTo(FieldPath.documentId(), mapId).get()
                        .addOnSuccessListener { documents ->

                            for (doc in documents) {
                                var map = doc.toObject(com.example.mapp.models.Map::class.java)
                                if(map.private == false) {
                                    var rowDataItem = DiscoveryListViewRow()
                                    rowDataItem.mapName = map.name
                                    rowDataItem.locationTag = map.locationTag
                                    rowDataItem.creator = map.creatorName
                                    rowDataItem.mapID = doc.id


                                    Log.d("Saved Mapps", rowDataItem.mapName)
                                    rowData.add(rowDataItem)
                                    adapter.notifyDataSetChanged()

                                }


                            }

                        }
                }


            }
            .addOnFailureListener { e ->
                Log.w("Saved Maps", "Error getting user",e)

            }



    }


}
