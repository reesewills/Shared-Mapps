package com.example.mapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.example.mapp.adapters.DiscoveryListViewAdapter
import com.example.mapp.models.DiscoveryListViewRow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_discover.*
import android.R.attr.data
import android.widget.*


class Discover : AppCompatActivity() {
    var rowData = ArrayList<DiscoveryListViewRow>()
    lateinit var listView: ListView
    lateinit var searchBar: EditText
    lateinit var radioGroup: RadioGroup
    lateinit var name_radio: RadioButton
    lateinit var city_radio: RadioButton
    lateinit var region_radio: RadioButton
    lateinit var country_radio: RadioButton
    lateinit var user_radio : RadioButton
    lateinit var adapter: DiscoveryListViewAdapter
    var searchTerm: String = ""
    var db = FirebaseFirestore.getInstance()
    //we shouldn't be exposing this key
    var client: Client = Client("BAL9441Y7Y", "c46566ac0de67d1281078979ac067499")
    var index: Index = client.getIndex("mapp")
    var firstQueryYet = false

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_discover -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_saved -> {
                val intent = Intent(this, MyMappsActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_followed -> {
                val intent = Intent(this, SavedMappsActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {

                val intent = Intent(this, UserInfoLogout::class.java)
                startActivity(intent)

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        this.listView = findViewById(R.id.list_view) as ListView
        adapter = DiscoveryListViewAdapter(this,rowData)
        listView.adapter = adapter
        this.searchBar = findViewById(R.id.search_bar)
        this.radioGroup = findViewById(R.id.radioGroup)
        name_radio = findViewById<RadioButton>(R.id.name_radio)
        city_radio = findViewById<RadioButton>(R.id.city_radio)
        region_radio = findViewById<RadioButton>(R.id.region_radio)
        country_radio = findViewById<RadioButton>(R.id.country_radio)
        user_radio = findViewById<RadioButton>(R.id.user_radio)



        searchBar.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                searchTerm = searchBar.text.toString()

                if(!searchTerm.isNullOrBlank()) {
                    search()

                    Log.d("Discover", "search attempted")

                }

                return@OnKeyListener true
            }
            false
        })

        radioGroup.setOnCheckedChangeListener { group, i ->

            if(!searchTerm.isNullOrBlank()) {
                search()
            }
        }




        //passes map id to the mapDisplay activity when a list view row is clicked
        listView.setOnItemClickListener {adapterView, view, i, l ->
            Log.d("Discover", i.toString()+" position clicked")
            val intent = Intent(this ,MapDisplay::class.java)
            intent.putExtra("mapID", rowData[i].mapID)
            intent.putExtra("activityName", "Discover")
            startActivity(intent)

        }



    }

    override fun onStart() {
        super.onStart()

        if (!firstQueryYet) {
            firstSearch()
        }

    }

    override fun onResume() {
        super.onResume()
        navigation.setSelectedItemId(R.id.navigation_discover)
    }

    fun search() {
        lateinit var searchBy: String

        if(name_radio.isChecked) {
            //redo search on names
            searchBy = "name"
        }
        if(city_radio.isChecked) {
            //redo search on city
            searchBy = "city"
        }
        if(region_radio.isChecked) {
            //redo search on regions
            searchBy = "region"
        }
        if(country_radio.isChecked) {
            //redo search on country
            searchBy = "country"
        }
        if(user_radio.isChecked) {
            //redo search on users
            searchBy = "creatorName"
        }

        db.collection("maps").whereEqualTo(searchBy, searchTerm).get()
            .addOnSuccessListener { documents ->

                rowData.clear()

                for(map in documents) {
                    var mapObj = map.toObject(com.example.mapp.models.Map::class.java)
                    if(mapObj.private == false) {
                        var rowDataItem = DiscoveryListViewRow()
                        rowDataItem.mapName = mapObj.name
                        rowDataItem.locationTag = mapObj.locationTag
                        rowDataItem.creator = mapObj.creatorName
                        rowDataItem.mapID = map.id



                        rowData.add(rowDataItem)
                        Log.d("1", rowData.size.toString())
                    }


                }
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                Log.w("QueryUtils", "Error: searchByCreator", exception)
            }

    }

    fun searchByCreator() {
        db.collection("maps").whereEqualTo("creatorName", searchTerm+"\\utf8ff").get()
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
                    Log.d("1", rowData.size.toString())

                }
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                Log.w("QueryUtils", "Error: searchByCreator", exception)
            }
    }

    fun searchByCityTag() {
        //TODO somehow search for both upper and lower case

        db.collection("maps").orderBy("locationTag").startAt(searchTerm).endAt(searchTerm+"\\utf8ff").get()
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
                    Log.d("1", rowData.size.toString())

                }
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                Log.w("QueryUtils", "Error: searchByCityTag", exception)
            }
    }



    private fun humanSearch(term: String) {

    }

    private fun firstSearch() {
        db.collection("maps").get().addOnSuccessListener { documents ->
            rowData.clear()

            for(map in documents) {
                var mapObj = map.toObject(com.example.mapp.models.Map::class.java)
                if(mapObj.private == false) {
                    var rowDataItem = DiscoveryListViewRow()
                    rowDataItem.mapName = mapObj.name
                    rowDataItem.locationTag = mapObj.locationTag
                    rowDataItem.creator = mapObj.creatorName
                    rowDataItem.mapID = map.id



                    rowData.add(rowDataItem)

                }


            }
            adapter.notifyDataSetChanged()
        }
    }






}
