package com.example.ecommerceapp.Models


import java.io.Serializable

annotation class Parcelize

@Parcelize
//Data class to hold the shoe data
data class ShoesItems (
    val id: Int,
    val name: String,
    val price:String,
    val image:String,
    val description:String,
    val quantity:String
):Serializable
