package com.example.chatapplication

import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
class Login : AppCompatActivity() {
    private lateinit var edtEmail:EditText
    private lateinit var edtPassword:EditText
    private lateinit var btnLogin:Button
    private lateinit var btnSignUp:Button
    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
            val email =edtEmail.text.toString()
            val password =edtPassword.text.toString()
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
                } else {
                    Toast.makeText(this, "User not Found", Toast.LENGTH_SHORT).show()
                }
            }
    }
}