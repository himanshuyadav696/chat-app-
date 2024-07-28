package com.example.chatapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

class MessageAdapter(val context:Context, private val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val item_recieve =1
    val item_send=2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType ==1){
            val view:View = LayoutInflater.from(context).inflate(R.layout.recieve,parent,false)
            return recieveViewHolder(view)
        }
        else
        {
            val view:View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        if(holder.javaClass == SentViewHolder::class.java){
            val currensendtmessage = messageList[position]
            val viewHolder = holder as SentViewHolder

            holder.sendmessage.text = currensendtmessage.message
            holder.tvSendTime.text = currensendtmessage.timeStamp?.let { Date(it) }
                ?.let { formatter.format(it) }?.capitalize()

            Log.e("TAG", "onBindViewHolder: ${currensendtmessage.timeStamp}", )
        }
        else
        {
            val currentrecmessage= messageList[position]
            val viewHolder = holder as recieveViewHolder
            holder.recievemessage.text = currentrecmessage.message
            holder.tvRecievTime.text = currentrecmessage.timeStamp?.let { Date(it) }
                ?.let { formatter.format(it) }?.capitalize()
            Log.e("TAG", "onBindViewHolder: ${currentrecmessage.timeStamp}", )
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentmessage = messageList[position]
        if(FirebaseAuth.getInstance().uid.equals(currentmessage.senderId))
        {
            return item_send
        }
        else
        {
            return item_recieve
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val sendmessage = itemView.findViewById<TextView>(R.id.txt_sentmessage)
        val tvSendTime = itemView.findViewById<TextView>(R.id.tvSendTime)
    }

    class recieveViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val recievemessage = itemView.findViewById<TextView>(R.id.txt_recievemessage)
        val tvRecievTime = itemView.findViewById<TextView>(R.id.tvRecieveTime)
    }
}