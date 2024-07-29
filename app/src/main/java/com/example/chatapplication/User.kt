package com.example.chatapplication

class User {

    var name:String? =null
    var email:String? =null
    var uid:String?=null
    var lastMessage:String?=null
    var online:Boolean?=null

    constructor()

    constructor(name:String?,email:String?,uid:String?,lastMessage: String?,online:Boolean?){
        this.name =name
        this.email =email
        this.uid = uid
        this.lastMessage = lastMessage
        this.online = online
    }
}