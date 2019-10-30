package com.example.mapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.Switch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_visibility_delete_map.*

class VisibilityDeleteMap : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    lateinit var mapID: String
    lateinit var mapName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visibility_delete_map)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        var saveButton = findViewById<Button>(R.id.save_button)
        var deleteButton = findViewById<Button>(R.id.delete_button)
        var privateOnSwitch = findViewById<Switch>(R.id.private_on_switch)


        mapID = intent.getStringExtra("mapID")
        mapName = intent.getStringExtra("mapName")
        map_name_text.text = mapName

        saveButton.setOnClickListener {
            val visibilityState = privateOnSwitch.isChecked

            //call to update database for the current map with visibility State
           val mapRef = db.collection("maps").document(mapID)

           mapRef.update("private", visibilityState)
               .addOnSuccessListener {
                   Log.d("VisibilityDeleteMap", "Updated map visibility")
               }
               .addOnFailureListener { e ->
                   Log.w("VisibilityDeleteMap", "Error updating visibility",e)

               }

            // go back to my maps activity

            val intent = Intent(this, MyMappsActivity::class.java)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {

            // dialog for confirmation

            lateinit var dialog: AlertDialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you really want to delete?")
            builder.setMessage("Map to be deleted: " + mapName)
            builder.setNegativeButton("Delete"){_, _ ->

                //delete query
                db.collection("maps").document(mapID).delete()
                    .addOnSuccessListener {
                        Log.d("VisibilityDeleteMap", "deleted map")
                    }
                    .addOnFailureListener { e->
                        Log.w("VisibilityDeleteMap", "error deleting map", e)
                    }
                // go back to my maps activity
                val intent = Intent(this, MyMappsActivity::class.java)
                startActivity(intent)
                dialog.dismiss()

            }

            builder.setNeutralButton("Cancel") {_, _ ->
                // go back to my maps activity
                val intent = Intent(this, MyMappsActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }


            dialog = builder.create()

            dialog.show()



        }



    }
}
