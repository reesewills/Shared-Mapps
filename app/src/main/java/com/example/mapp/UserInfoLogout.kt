package com.example.mapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_info_logout.*

class UserInfoLogout : AppCompatActivity() {
    var mAuth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_logout)

        logout_buton.setOnClickListener {
            val intent = Intent(this ,Login::class.java)
            intent.flags =  Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            mAuth.signOut()


        }


        db.collection("users").whereEqualTo("email", mAuth.currentUser?.email).get()
            .addOnSuccessListener { documents->
                for(doc in documents) {
                    username_text.text = doc.get("username") as String
                    email_text.text = mAuth.currentUser?.email
                }
            }


    }
}
