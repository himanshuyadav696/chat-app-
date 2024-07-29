package com.example.chatapplication
import SharedPreferencesHelper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class chatActivty : AppCompatActivity() {

    private lateinit var messageRecycerView:RecyclerView
    private lateinit var messagebox:EditText
    private lateinit var sendbutton:ImageView
    private lateinit var backButton:ImageView
    private lateinit var userName:TextView
    private lateinit var tvOnline:TextView
    private lateinit var btnSelectImage:ImageView
    private lateinit var myDbref:DatabaseReference

    private lateinit var messageList:ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    var recieverroom:String?=null
    var senderroom:String?=null
    var senderUid:String?=null
    private lateinit var prefsHelper: SharedPreferencesHelper

    var lastMessage:String?="loading..."

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_activty)
         prefsHelper = SharedPreferencesHelper(this)
         supportActionBar?.hide()
        messageRecycerView = findViewById(R.id.chatRecylerview)
        messagebox =findViewById(R.id.messageBox)
        sendbutton = findViewById(R.id.btnsend)
         backButton = findViewById(R.id.ivBack)
         userName = findViewById(R.id.tvUserName)
         tvOnline = findViewById(R.id.tvOnline)
         btnSelectImage = findViewById(R.id.ivSelectImage)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        messageRecycerView.layoutManager =LinearLayoutManager(this)
        messageRecycerView.adapter = messageAdapter
        myDbref = FirebaseDatabase.getInstance().getReference()

         btnSelectImage.setOnClickListener {
             val intent = Intent()
             intent.setType("image/*")
             intent.setAction(Intent.ACTION_GET_CONTENT)
             startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
         }

         backButton.setOnClickListener {
             onBackPressed()
         }
         val name= intent.getStringExtra("name")
         Log.e("name", "onCreate:name $name")
         val recieverUid=intent.getStringExtra("Uid")
         senderUid = FirebaseAuth.getInstance().currentUser?.uid

         val currentUserId =  prefsHelper.getString("userId")
         Log.e("TAG", "onCreate: $currentUserId $senderUid")

         val onlineStatus = intent.getBooleanExtra("onlineStatus",false)
         Log.e("TAG", "onCreate:bool $onlineStatus")

         if(onlineStatus==true){
             tvOnline.text = "Online"
         }else{
             tvOnline.text = "Not active"
         }

         userName.text = name
        senderroom = recieverUid+senderUid
        recieverroom = senderUid+recieverUid

        myDbref .child("chats").child(senderroom!!).child("messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postsnapshot in snapshot.children){
                        val message = postsnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageRecycerView.smoothScrollToPosition(messageAdapter.itemCount-1)
                    messageAdapter.notifyDataSetChanged()
                    if(messageList.isNotEmpty()){
                        lastMessage =  messageList.last().message
                    }
                    setUserOnlineStatus(true)
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

    private fun setUserOnlineStatus(isOnline: Boolean) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val userStatusRef = myDbref.child("user").child(it.uid).child("online")
            val userLastMessageRef = myDbref.child("user").child(it.uid).child("lastMessage")
            if (isOnline) {
                userStatusRef.onDisconnect().setValue(false)
            }
            val updates = mapOf(
                "online" to isOnline,
                "lastMessage" to lastMessage
            )
            myDbref.child("user").child(it.uid).updateChildren(updates)
        }
    }

    override fun onStop() {
        super.onStop()
        setUserOnlineStatus(false)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val image = data?.data
            if (image != null) {
                uploadImageToFirebase(image)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    val messageObject = Message(uri.toString(), senderUid, System.currentTimeMillis())
                    myDbref.child("chats").child(senderroom!!).child("messages").push()
                        .setValue(messageObject)
                        .addOnSuccessListener {
                            myDbref.child("chats").child(recieverroom!!).child("messages").push()
                                .setValue(messageObject)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Image upload failed", e)
            }
    }
}