package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var userrecyclerview:RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var Adapter:UserAdapter
    private lateinit var mAuth:FirebaseAuth

    private lateinit var mDbref :DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbref = FirebaseDatabase.getInstance().getReference()
        userrecyclerview = findViewById(R.id.userRecylerView)
        userList = ArrayList()
        Adapter = UserAdapter(this,userList)

        userrecyclerview.layoutManager =LinearLayoutManager(this)
        userrecyclerview.adapter =Adapter
        mDbref.child("user").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //userList.clear()
               for(postsnapshot in snapshot.children){

                   val currentuser = postsnapshot.getValue(User::class.java)

                   if(mAuth.currentUser?.uid!=currentuser?.uid)
                   {
                       userList.add(currentuser!!)
                   }
               }
                Adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    /*private fun updateOnlineStatus() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().getReference("user").child(userId!!)
        userRef.child("online").setValue(true)
        userRef.child("online").onDisconnect().setValue(false)
    }
*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout)
        {
            mAuth.signOut()
            finish()
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            return true
        }
        return true
    }
}