package com.example.mapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.io.Serializable

class Login : AppCompatActivity() {

    lateinit var  mAuth : FirebaseAuth
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        login_button.setOnClickListener {
            email = login_email.text.toString()
            password = login_password.text.toString()

            if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
                login(email, password)
            }
            else {
                Toast.makeText(baseContext, "Email and Password are required", Toast.LENGTH_SHORT).show()
            }
        }

        login_register_button.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)

        }
    }

    private fun login(email:String, password:String) {
        Log.d("Login", email)

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                Log.d("Login", "success")

                val intent = Intent(this, Discover::class.java)
                //intent.putExtra("fbAuth", mAuth as Serializable)
                startActivity(intent)
                finish()


            }
            else {
                Log.d("Login", "failed")
                Toast.makeText(baseContext, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
