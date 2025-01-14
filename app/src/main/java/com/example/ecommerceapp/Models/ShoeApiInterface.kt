package com.example.ecommerceapp.Models

import retrofit2.Call
import retrofit2.http.GET

interface ShoeApiInterface {
    @GET("shoe_586140440649231")
    fun getShoes(): Call<ShoesResponse>
}