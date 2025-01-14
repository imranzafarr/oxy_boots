package com.example.ecommerceapp.Models



@Parcelize
//Data class to hold the shoe data
data class FavoriteItems (
    val uid: String="",
    val name: String="",
    val price:String="",
    val image:String="",
    val description:String="",
    val quantity:String=""
)