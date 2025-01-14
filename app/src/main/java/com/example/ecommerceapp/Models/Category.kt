package com.example.ecommerceapp.Models

import java.io.Serializable

@Parcelize
data class Category (
    val id:Int,
    val category:String,
    val products:List<ShoesItems>
): Serializable
