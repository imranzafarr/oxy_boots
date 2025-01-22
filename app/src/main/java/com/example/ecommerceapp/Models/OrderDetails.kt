package com.example.ecommerceapp.Models
@Parcelize
data class OrderDetails(
    val orderId:Long = 0L,
    val uid:String="",
    val name:String="",
    val email:String="",
    val phone:String="",
    val address:String="",
    val subTotal:Int=0,
    val shippingFee:Int=0,
    val totalCost:Int=0,
    val items:String="",
    val paymentMethod:String="",
    val orderDate:String="",
    val deliveryDate: String = "",
    val cartItems:List<Cart> = emptyList(),
)