package com.example.chatapplication

import java.sql.Timestamp

class Message {

    var message:String?=null
    var senderId:String? =null
    var timeStamp:Long? = null

    constructor(){}

    constructor(message:String?,senderId:String?,time: Long?){
        this.message =message
        this.senderId = senderId
        this.timeStamp = time
    }
}