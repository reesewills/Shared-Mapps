package com.example.mapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import com.example.mapp.models.Pin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_create_pin.*
import kotlinx.android.synthetic.main.activity_visibility_delete_map.*

class ActivityEditCreatePin: AppCompatActivity() {
    private lateinit var pin: Pin
    private lateinit var viewModel: MapViewModel
    private lateinit var mapid: String
    var db = FirebaseFirestore.getInstance()
    lateinit var  mAuth : FirebaseAuth

    //ALWAYS PASS IN PIN ID OR IT WILL CRASH, -1 IF WE DONT HAVE ONE YET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_create_pin)
        //track = Track(intent.extras!!.getSerializable("TRACK_NAME").toString(), intent.extras!!.getSerializable("TRACK_ARTIST").toString(), intent.extras!!.getSerializable("TRACK_URL").toString(), intent.extras!!.getSerializable("TRACK_IMAGE_URL").toString(), intent.extras!!.getSerializable("TRACK_MBID").toString(), intent.extras!!.getSerializable("TRACK_PLAYCOUNT").toString())
        if(intent.extras!!.getSerializable("PIN_NAME").toString() != null){
            if(intent.extras!!.getSerializable("PIN_DESCRIPTION").toString() != null){
                    if(intent.extras!!.getSerializable("PIN_LATITUDE").toString() != null){
                        if(intent.extras!!.getSerializable("PIN_LONGITUDE").toString() != null){
                            if(intent.extras!!.getSerializable("PIN_ID").toString() != null){
                                pin = Pin(intent.extras!!.getSerializable("PIN_ID").toString().toInt(), intent.extras!!.getSerializable("PIN_NAME").toString(), intent.extras!!.getSerializable("PIN_DESCRIPTION").toString(), intent.extras!!.getSerializable("PIN_LATITUDE").toString().toDouble(), intent.extras!!.getSerializable("PIN_LONGITUDE").toString().toDouble())
                                pin_name.setText(intent.extras!!.getSerializable("PIN_NAME").toString())
                                pin_description.setText(intent.extras!!.getSerializable("PIN_DESCRIPTION").toString())
                            }
                        }
                    }
            }
        }
        if(intent.extras!!.getSerializable("MAP_ID").toString() != null){
            mapid = intent.extras!!.getSerializable("MAP_ID").toString()
            pin.mapid = mapid
        }


        var para = findViewById<ImageView>(R.id.ic_paragliding)
        var cross = findViewById<ImageView>(R.id.ic_cross)
        var pinIcon = findViewById<ImageView>(R.id.ic_pin)
        var burger = findViewById<ImageView>(R.id.ic_burger)
        var lodging = findViewById<ImageView>(R.id.ic_lodging)
        var tree = findViewById<ImageView>(R.id.ic_tree)
        var car = findViewById<ImageView>(R.id.ic_car)
        var airport = findViewById<ImageView>(R.id.ic_airport)

        if (!intent.getStringExtra("DB_PIN_ID").isNullOrBlank()) {
            pin.dbPinId = intent.getStringExtra("DB_PIN_ID")
            pin.icon = intent.getStringExtra("IMG")

            if (pin.icon == "ic_car") {
                car.setBackgroundColor(Color.GREEN)

            }
            if (pin.icon == "ic_airport") {
                airport.setBackgroundColor(Color.GREEN)

            }
            if (pin.icon == "ic_tree") {
                tree.setBackgroundColor(Color.GREEN)

            }
            if (pin.icon == "ic_lodging") {
                lodging.setBackgroundColor(Color.GREEN)

            }
            if (pin.icon == "ic_burger") {
                burger.setBackgroundColor(Color.GREEN)

            }
            if (pin.icon == "ic_pin") {
                pinIcon.setBackgroundColor(Color.GREEN)

            }
            if (pin.icon == "ic_cross") {
                cross.setBackgroundColor(Color.GREEN)

            }
            if (pin.icon == "ic_paragliding") {
                para.setBackgroundColor(Color.GREEN)

            }
        }


        delete_button_pin.setOnClickListener {

            if (!pin.dbPinId.isNullOrBlank()) {
                deletePin()
            }



            val intent = Intent(this ,MapDisplay::class.java)
            intent.putExtra("mapID", mapid)
            intent.putExtra("activityName", "ActivityEditCreatePin")
            startActivity(intent)
            this.finish()

        }
        confirm_button.setOnClickListener {
            pin.name = pin_name.text.toString()
            pin.description = pin_description.text.toString()
            println("EDIT PIN HERE")
            println(pin)
            println(pin.pinID)
            println(mapid)
            println(pin.name)

            if (pin.pinID == -1) {
                pin.pinID = 1
                createPin()
            }
            else {
                updatePin()
            }




            val intent = Intent(this ,MapDisplay::class.java)
            intent.putExtra("mapID", mapid)
            intent.putExtra("activityName", "ActivityEditCreatePin")
            startActivity(intent)

            this.finish()

        }



        ic_airport.setOnClickListener {
            pin.icon ="ic_airport"
            airport.setBackgroundColor(Color.GREEN)


            para.setBackgroundColor(Color.TRANSPARENT)
            cross.setBackgroundColor(Color.TRANSPARENT)
            pinIcon.setBackgroundColor(Color.TRANSPARENT)
            burger.setBackgroundColor(Color.TRANSPARENT)
            lodging.setBackgroundColor(Color.TRANSPARENT)
            tree.setBackgroundColor(Color.TRANSPARENT)
            car.setBackgroundColor(Color.TRANSPARENT)

        }
        ic_car.setOnClickListener {
            pin.icon ="ic_car"

            car.setBackgroundColor(Color.GREEN)
            airport.setBackgroundColor(Color.TRANSPARENT)
            para.setBackgroundColor(Color.TRANSPARENT)
            cross.setBackgroundColor(Color.TRANSPARENT)
            pinIcon.setBackgroundColor(Color.TRANSPARENT)
            burger.setBackgroundColor(Color.TRANSPARENT)
            lodging.setBackgroundColor(Color.TRANSPARENT)
            tree.setBackgroundColor(Color.TRANSPARENT)

        }
        ic_tree.setOnClickListener {
            pin.icon ="ic_tree"

            tree.setBackgroundColor(Color.GREEN)
            airport.setBackgroundColor(Color.TRANSPARENT)
            para.setBackgroundColor(Color.TRANSPARENT)
            cross.setBackgroundColor(Color.TRANSPARENT)
            pinIcon.setBackgroundColor(Color.TRANSPARENT)
            burger.setBackgroundColor(Color.TRANSPARENT)
            lodging.setBackgroundColor(Color.TRANSPARENT)

            car.setBackgroundColor(Color.TRANSPARENT)
        }
        ic_lodging.setOnClickListener {
            pin.icon ="ic_lodging"

            lodging.setBackgroundColor(Color.GREEN)
            airport.setBackgroundColor(Color.TRANSPARENT)
            para.setBackgroundColor(Color.TRANSPARENT)
            cross.setBackgroundColor(Color.TRANSPARENT)
            pinIcon.setBackgroundColor(Color.TRANSPARENT)
            burger.setBackgroundColor(Color.TRANSPARENT)

            tree.setBackgroundColor(Color.TRANSPARENT)
            car.setBackgroundColor(Color.TRANSPARENT)
        }
        ic_burger.setOnClickListener {
            pin.icon ="ic_burger"

            burger.setBackgroundColor(Color.GREEN)
            airport.setBackgroundColor(Color.TRANSPARENT)
            para.setBackgroundColor(Color.TRANSPARENT)
            cross.setBackgroundColor(Color.TRANSPARENT)
            pinIcon.setBackgroundColor(Color.TRANSPARENT)

            lodging.setBackgroundColor(Color.TRANSPARENT)
            tree.setBackgroundColor(Color.TRANSPARENT)
            car.setBackgroundColor(Color.TRANSPARENT)
        }
        ic_pin.setOnClickListener {
            pin.icon ="ic_pin"

            pinIcon.setBackgroundColor(Color.GREEN)
            airport.setBackgroundColor(Color.TRANSPARENT)
            para.setBackgroundColor(Color.TRANSPARENT)
            cross.setBackgroundColor(Color.TRANSPARENT)

            burger.setBackgroundColor(Color.TRANSPARENT)
            lodging.setBackgroundColor(Color.TRANSPARENT)
            tree.setBackgroundColor(Color.TRANSPARENT)
            car.setBackgroundColor(Color.TRANSPARENT)
        }
        ic_cross.setOnClickListener {
            pin.icon ="ic_cross"

            cross.setBackgroundColor(Color.GREEN)
            airport.setBackgroundColor(Color.TRANSPARENT)
            para.setBackgroundColor(Color.TRANSPARENT)

            pinIcon.setBackgroundColor(Color.TRANSPARENT)
            burger.setBackgroundColor(Color.TRANSPARENT)
            lodging.setBackgroundColor(Color.TRANSPARENT)
            tree.setBackgroundColor(Color.TRANSPARENT)
            car.setBackgroundColor(Color.TRANSPARENT)
        }
        ic_paragliding.setOnClickListener {
            pin.icon ="ic_paragliding"

            para.setBackgroundColor(Color.GREEN)
            airport.setBackgroundColor(Color.TRANSPARENT)

            cross.setBackgroundColor(Color.TRANSPARENT)
            pinIcon.setBackgroundColor(Color.TRANSPARENT)
            burger.setBackgroundColor(Color.TRANSPARENT)
            lodging.setBackgroundColor(Color.TRANSPARENT)
            tree.setBackgroundColor(Color.TRANSPARENT)
            car.setBackgroundColor(Color.TRANSPARENT)
        }
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    override fun onBackPressed() {
        this.finish()
    }

    private fun createPin() {
        db.collection("pins").add(pin).addOnSuccessListener {
            val intent = Intent(this ,MapDisplay::class.java)
            intent.putExtra("mapID", mapid)
            intent.putExtra("activityName", "ActivityEditCreatePin")
            startActivity(intent)
            finish()
        }
            .addOnFailureListener { e->
                Log.w("Add Pin", "failed to add pin",e)
            }
    }

    private fun updatePin() {
        db.collection("pins").whereEqualTo("mapid", pin.mapid).get().addOnSuccessListener { userResult ->

            for (document in userResult) {

                if(pin.dbPinId == document.id){

                    db.collection("pins").document(document.id).delete().addOnSuccessListener {
                        Log.d("WAHAHAHAH", pin.dbPinId + " " + document.id)
                    }

                }
            }

            pin.dbPinId = ""

            db.collection("pins").add(pin).addOnSuccessListener {
                val intent = Intent(this ,MapDisplay::class.java)
                intent.putExtra("mapID", mapid)
                intent.putExtra("activityName", "ActivityEditCreatePin")
                startActivity(intent)
                finish()
            }
                .addOnFailureListener { e->
                    Log.w("Add Pin", "failed to update pin",e)
                }


        }

    }

    private fun deletePin() {
        Log.d("Edit Ccreate", "delete pin")
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        db.collection("pins").whereEqualTo("mapid", pin.mapid).get().addOnSuccessListener { userResult ->

            for (document in userResult) {

                if(pin.dbPinId == document.id){

                    db.collection("pins").document(document.id).delete().addOnSuccessListener {
                        Log.d("WAHAHAHAH", pin.dbPinId + " " + document.id)
                    }

                }
            }
            val intent = Intent(this ,MapDisplay::class.java)
            intent.putExtra("mapID", mapid)
            intent.putExtra("activityName", "ActivityEditCreatePin")
            startActivity(intent)
            finish()

        }
        }
    }



