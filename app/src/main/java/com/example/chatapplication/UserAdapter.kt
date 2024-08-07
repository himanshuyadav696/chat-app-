package com.example.chatapplication
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class UserAdapter(val context:Context,val userList:ArrayList<User>):RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser =  userList[position]
        holder.txtname.text = currentUser.name
        holder.lastMessage.text = currentUser.lastMessage


        Log.e("TAG", "onBindViewHolder: ${currentUser.online}", )


        val database = FirebaseDatabase.getInstance().reference.child("chats").get()
        Log.e("TAG", "onBindViewHolder: $database", )


        holder.itemView.setOnClickListener{
            val intent = Intent(context,chatActivty::class.java)
            intent.putExtra("name",currentUser.name)
            intent.putExtra("Uid",currentUser.uid)
            intent.putExtra("onlineStatus",currentUser.online)
            Toast.makeText(context, "${currentUser.online}", Toast.LENGTH_SHORT).show()
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class  UserViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val txtname = itemView.findViewById<TextView>(R.id.txt_name)
        val lastMessage = itemView.findViewById<TextView>(R.id.tvLastMessage)
    }

}