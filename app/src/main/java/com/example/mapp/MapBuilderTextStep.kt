package com.example.mapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mapp.models.Map
import com.example.mapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_map_builder_text_step.*

class MapBuilderTextStep : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser
    lateinit var username: String
    lateinit var userCollectionUID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_builder_text_step)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        db = FirebaseFirestore.getInstance()


        //get username, UID
        db.collection("users").whereEqualTo("email", currentUser.email).get()
            .addOnSuccessListener { documents ->
                for (user in documents) {
                    var userObj = user.toObject(User::class.java)
                    username = userObj.username
                    userCollectionUID = user.id
                }
            }

        create_button.setOnClickListener {

            val name = map_name_input.text.toString()
            val city = city_input.text.toString()
            val region = region_input.text.toString()
            val country = country_input.text.toString()
            val private = private_on_switch.isChecked

            if (name.isNullOrBlank() || city.isNullOrBlank() || region.isNullOrBlank() || country.isNullOrBlank()) {
                Toast.makeText(baseContext, "All fields are required", Toast.LENGTH_SHORT).show()
            }
            else {
                var map = Map()
                map.name = name
                map.locationTag = city + ", " + region
                map.city = city
                map.region = region
                map.country = country
                map.private = private
                map.creatorName = username
                map.creatorID = userCollectionUID

                // add map to data base and on success open new activity

                db.collection("maps").add(map)
                    .addOnSuccessListener { documentReference ->
                        Log.d("MapBuilderTextStep", "DocumentSnapshot written with ID: ${documentReference.id}")
                        //start the pin adding map activity
                        val intent = Intent(this ,MapDisplay::class.java)
                        intent.putExtra("mapID", documentReference.id)
                        intent.putExtra("activityName", "MapBuilderTextStep")
                        startActivity(intent)
                        finish()

                }
                    .addOnFailureListener { e ->
                        Log.w("MapBuilderTextStep", "Error adding document", e)
                    }


            }

        }
    }

}
