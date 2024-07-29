package com.example.chatapplication

import SharedPreferencesHelper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {
    private lateinit var edtEmail:EditText
    private lateinit var edtPassword:EditText
    private lateinit var btnLogin:Button
    private lateinit var btnSignUp:Button
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mDbref: DatabaseReference
    private lateinit var prefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        prefsHelper = SharedPreferencesHelper(this)
        mAuth = FirebaseAuth.getInstance()

        edtEmail =findViewById(R.id.edt_email)
        edtPassword =findViewById(R.id.edt_password)
        btnLogin =findViewById(R.id.btnLogin)
        btnSignUp =findViewById(R.id.btnSignUp)
        supportActionBar?.hide()

        btnSignUp.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            checkValidation()
        }
    }

    private fun checkValidation() {
        val email =edtEmail.text.toString()
        val password =edtPassword.text.toString()
        if(email.isNullOrEmpty()){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
        }
        else if(password.isNullOrEmpty()){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
        }
        else{
            login(email,password)
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this,MainActivity::class.java)
                    finish()
                    startActivity(intent)
                    mDbref = FirebaseDatabase.getInstance().getReference()
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val userRef = FirebaseDatabase.getInstance().getReference("user").child(userId!!)
                    userRef.child("online").setValue(true)
                    prefsHelper.saveBoolean("isLogin",true)
                    prefsHelper.saveString("userId",userId)

                } else {
                    Toast.makeText(this, "User not Found", Toast.LENGTH_SHORT).show()
                }
            }
    }
}