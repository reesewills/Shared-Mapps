package com.example.mapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mapp.models.Pin
import com.example.mapp.models.Map
import com.example.mapp.models.User
import kotlinx.android.synthetic.main.activity_map_display.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore



class MapDisplay : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    private lateinit var mMap: GoogleMap
    private var markerList: ArrayList<Marker> = ArrayList()
    private var pinList: ArrayList<Pin> = ArrayList()
    private lateinit var map: Map
    private var editMode: Boolean = false
    private lateinit var mapID: String
    lateinit var mAuth : FirebaseAuth
    lateinit var user: User
    lateinit var userId: String

    var db = FirebaseFirestore.getInstance()

    //onCreate - loads Map activity, enables or disables edit mode depending on what activity the map is loaded from (and permissions the user has)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        mAuth = FirebaseAuth.getInstance()


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var activityName = ""
        try {
            mapID = intent.getStringExtra("mapID")
            activityName = intent.getStringExtra("activityName")
        } catch (e : Exception) {
            Log.e("MapDisplay", "Error loading map intent, perhaps you opened the map from the navigation bar button")
            //TODO: kick user out of mapdisplay activity and display an error message (maybe), since this means we weren't passed a mapID at all, shouldn't ever happen tho
        }

        loadPinList()


        //if the activityName is MapBuilderTextStep or the pin edit/create activity then turn editing on
        if (activityName == "MapBuilderTextStep" || activityName  == "ActivityEditCreatePin") {
            editMode = true
            edit_done_button.visibility = View.VISIBLE
            edit_done_button.text = "Done"

        }
        //if we're loading it from MyMapps, we want the option to edit, but don't want it to be default
        if (activityName == "MyMappsActivity") {
            edit_done_button.visibility = View.VISIBLE
            edit_done_button.text = "Edit"
        }

        edit_done_button.setOnClickListener {
            if (editMode) {
                edit_done_button.text = "Edit"
                editMode = false
            }
            else {
                edit_done_button.text = "Done"
                editMode = true
            }
        }

        follow_button.setOnClickListener {
            if(follow_button.text == "Unfollow") {
                //db call to unfollow
                db.collection("users").document(userId).update("followedMaps", FieldValue.arrayRemove(mapID)).addOnSuccessListener {
                    follow_button.text = "Folow"
                    Toast.makeText(baseContext, "Map Unfollowed!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e->
                    Log.w("MapDisplay", "could not unfollow", e)
                }

            }
            else {
                //db call to follow
                db.collection("users").document(userId).update("followedMaps", FieldValue.arrayUnion(mapID)).addOnSuccessListener {
                    follow_button.text = "Unfollow"
                    Toast.makeText(baseContext, "Map Followed!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Log.w("MapDisplay", "could not follow", e)
                }

            }
        }

        clone_button.setOnClickListener {
            //db call to clone map

            var newMap = map
            newMap.creatorID = userId
            newMap.creatorName = user.username
            newMap.name = newMap.name + "_clone"

            db.collection("maps").add(newMap).addOnSuccessListener {
                Toast.makeText(baseContext, "Map Cloned!!", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e->
                    Log.w("MapDisplay", "could not clone", e)
                }

        }


        Log.d("DB", "Map ID given: " + mapID)

        //here is where we load the map from the database
        db.collection("maps").whereEqualTo(FieldPath.documentId(), mapID).get()
            .addOnSuccessListener { documents ->
                for(dbMap in documents) {
                    var mapObj = dbMap.toObject(Map::class.java)
                    Log.d("DB", "Here's what's in that object:")
                    Log.d("DB", mapObj.toString())
                    map = mapObj
                }
                drawPins()
                if (activityName == "Discover" || activityName == "SavedMappsActivity") {
                    db.collection("users").whereEqualTo("email", mAuth.currentUser?.email).get()
                        .addOnSuccessListener { documents ->
                            for (doc in documents) {
                                user = doc.toObject(User::class.java)
                                userId = doc.id
                                if(user.username == map.creatorName) {
                                    //  show the edit button
                                    edit_done_button.visibility = View.VISIBLE
                                }
                                else {
                                    follow_button.visibility = View.VISIBLE
                                    clone_button.visibility = View.VISIBLE
                                   if(user.followedMaps.contains(mapID)) {
                                       follow_button.text = "Unfollow"
                                   }

                                }

                            }
                        }
                }
            }

    }

    override fun onResume() {
        super.onResume()
//        for(marker in markerList) {
//            markerDelete(marker)
//        }
        loadPinList()
    }

    //load the appropriate pins for the map from the DB
    fun loadPinList() {
        Log.d("DB", "Starting loading of pins now.")
        pinList = ArrayList()
        db.collection("pins").whereEqualTo("mapid", mapID).get().addOnSuccessListener { documents ->
            for(pin in documents) {
                var pinObj = pin.toObject(Pin::class.java)
                pinObj.dbPinId = pin.id
                pinList.add(pinObj)
                Log.d("DB", "loaded pin object from DB, added it to pinList. Pin description: " + pin.get("description"))
            }
            drawPins()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnInfoWindowClickListener(this)
        mMap.setOnMapLongClickListener {LatLng -> Unit
            if(editMode) {

                val intent = Intent(this ,ActivityEditCreatePin::class.java)
                intent.putExtra("PIN_NAME", "")
                intent.putExtra("PIN_DESCRIPTION", "")
                intent.putExtra("PIN_LATITUDE", LatLng.latitude)
                intent.putExtra("PIN_LONGITUDE", LatLng.longitude)
                intent.putExtra("PIN_ID", -1)
                intent.putExtra("MAP_ID", mapID)
                startActivity(intent)
                this.finish()
            }

            Log.d("MapDisplay", "LongClick @ Lat: " + LatLng.latitude.toString() + ", Long: " +LatLng.longitude.toString())


        }

        //TODO move camera to the right place

    }

    private fun drawPins() {
        if (::mMap.isInitialized) {
            markerList = ArrayList()
            for(pin in pinList) {
               makeMarker(pin)
            }
        }
    }

    private fun makeMarker(pin: Pin) {
        if(pin.icon == "") {
            pin.icon = "ic_pin"
        }
        val id = this.resources.getIdentifier(pin.icon, "drawable", this.packageName)
        val bitmapDescriptor = BitmapDescriptorFactory.fromResource(id)
        val newMarker = mMap.addMarker(MarkerOptions().position(pin.pinGPSLocation()).title(pin.name).snippet(pin.description).icon(bitmapDescriptor))
        newMarker.tag = pin.dbPinId

        Log.d("DB", "Adding marker to map for pinID " + pin.pinID)

        markerList.add(newMarker)
    }


    override fun onInfoWindowClick(marker: Marker?) {
        if(editMode) {
            var pinID = marker?.tag
            for (pin in pinList) {
                if (pin.dbPinId == pinID) {
                    val intent = Intent(this, ActivityEditCreatePin::class.java)
                    intent.putExtra("PIN_NAME", pin.name)
                    intent.putExtra("PIN_DESCRIPTION", pin.description)
                    intent.putExtra("PIN_LATITUDE", pin.latitude)
                    intent.putExtra("PIN_LONGITUDE", pin.longitude)
                    intent.putExtra("PIN_ID", pin.pinID)
                    intent.putExtra("MAP_ID", mapID)
                    intent.putExtra("DB_PIN_ID", pin.dbPinId)
                    intent.putExtra("IMG", pin.icon)
                    startActivity(intent)
                    finish()

                    this.finish()

                }
            }
        }
        else {
            //TODO: add an info window activity to bring up more detail on the pin, call it here (i.e. when not in edit mode)
        }
    }

    private fun markerDelete(marker: Marker?) {
        Log.d("PINS", "Marker/pin deletion incoming!")
        Log.d("PINS", "Current pinList size: " + pinList.size + ", current markerList size: " + markerList.size)
        var pinToRemove = pinList[0]
        for(pin in pinList) {
            if(pin.dbPinId == marker?.tag) {
                Log.d("PINS", "Pin match! Pin description: " + pin.description)
                Log.d("PINS", "Matching marker description: " + marker.snippet)
                pinToRemove = pin
            }
        }
        pinList.remove(pinToRemove)
        for(existingMarker in markerList) {
            if(existingMarker.id == marker?.id) {
                Log.d("PINS", "Marker exists in markerList and will be removed")
            }
        }
        markerList.remove(marker)
        marker?.remove()
        Log.d("PINS", "New pinList size: " + pinList.size + ", new markerList size: " + markerList.size)
    }
    override fun onBackPressed() {
        finish()
    }
}


