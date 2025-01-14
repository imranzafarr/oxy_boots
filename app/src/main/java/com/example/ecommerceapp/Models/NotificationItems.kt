package com.example.ecommerceapp.Models

data class NotificationItems (
    val uid:String="",
    val title:String="",
    val message:String="",
    val timestamp: Long = System.currentTimeMillis(),
    val number:Int=0
)