package com.example.mapp


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.widget.Toast
import com.example.mapp.models.Map
import com.example.mapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    lateinit var  mAuth : FirebaseAuth
    lateinit var email: String
    lateinit var password: String
    lateinit var username: String

    var db : FirebaseFirestore = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        register_make_account_button.setOnClickListener {
            email = register_email.text.toString()
            password = register_password.text.toString()
            username = register_username.text.toString()


            if (!email.isNullOrBlank() && ! password.isNullOrBlank() && !username.isNullOrBlank()) {
                createAccount(email,username,password)
            }
            else {
                Toast.makeText(baseContext, "Email, Password and Username are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount(email:String, username: String, password: String) {
        Log.d("register", email)
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("register", "success")
                var user: User = User()
                user.email = email
                user.username = username
                user.followedMaps = arrayListOf<String>()




                db.collection("users").add(user)
                    .addOnSuccessListener {
                        Log.d("register", "username saved")
                    }
                    .addOnFailureListener {
                        Log.d("register", "failed to save username")
                    }
                val intent = Intent(baseContext, Login::class.java)
                startActivity(intent)

                Toast.makeText(baseContext, "Successfully created account, please login", Toast.LENGTH_SHORT).show()

            }
            else {
                Log.d("register", "failed")

                Toast.makeText(baseContext, "Registration Failed", Toast.LENGTH_SHORT).show()

            }
        }
    }
}

