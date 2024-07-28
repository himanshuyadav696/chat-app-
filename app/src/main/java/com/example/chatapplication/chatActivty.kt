package com.example.chatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class chatActivty : AppCompatActivity() {

    private lateinit var messageRecycerView:RecyclerView
    private lateinit var messagebox:EditText
    private lateinit var sendbutton:ImageView
    private lateinit var backButton:ImageView
    private lateinit var userName:TextView
    private lateinit var myDbref:DatabaseReference

    private lateinit var messageList:ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    var recieverroom:String?=null
    var senderroom:String?=null

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_activty)
         supportActionBar?.hide()
        messageRecycerView = findViewById(R.id.chatRecylerview)
        messagebox =findViewById(R.id.messageBox)
        sendbutton = findViewById(R.id.btnsend)
         backButton = findViewById(R.id.ivBack)
         userName = findViewById(R.id.tvUserName)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        messageRecycerView.layoutManager =LinearLayoutManager(this)
        messageRecycerView.adapter = messageAdapter
        myDbref = FirebaseDatabase.getInstance().getReference()


         backButton.setOnClickListener {
             onBackPressed()
         }
        // val intent = Intent()
       val name= intent.getStringExtra("name")
         Log.e("name", "onCreate:name $name", )
        val recieverUid=intent.getStringExtra("Uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid


         userName.text = name
        senderroom = recieverUid+senderUid
        recieverroom = senderUid+recieverUid

        //logiv to add the message into the recyclerview
        myDbref .child("chats").child(senderroom!!).child("messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postsnapshot in snapshot.children){
                        val message = postsnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        //adding the messages to database
        sendbutton.setOnClickListener{
            val message = messagebox.text.toString()
            val messageobject = Message(message,senderUid,System.currentTimeMillis())
            myDbref.child("chats").child(senderroom!!).child("messages").push()
                .setValue(messageobject)
                .addOnSuccessListener {
                    myDbref.child("chats").child(recieverroom!!).child("messages").push()
                        .setValue(messageobject)
                }
            messagebox.setText("")
        }

    }

}