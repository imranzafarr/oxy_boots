package com.example.ecommerceapp.Models

@Parcelize
data class CartItems(
    val uid: String="",
    val image:String="",
    val name: String="",
    val description:String="",
    val quantity:String="",
    val price:String="",
    val size:String="",
    val color:String=""
)