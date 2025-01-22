package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Models.Cart
import com.example.ecommerceapp.R

class ItemsAdapter(private val cartItems: List<Cart>) :
    RecyclerView.Adapter<ItemsAdapter.CartItemViewHolder>() {

    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.shoeImage)
        val productName: TextView = itemView.findViewById(R.id.shoeName)
        val productQuantity: TextView = itemView.findViewById(R.id.quantity)
        val productSize: TextView = itemView.findViewById(R.id.shoeSize)
        val productColor: TextView = itemView.findViewById(R.id.shoeColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_details, parent, false)
        return CartItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]

        // Loading image using your preferred image loading library (Glide/Picasso)
         Glide.with(holder.itemView.context).load(cartItem.image).into(holder.productImage)
        holder.productName.text = cartItem.name
        holder.productQuantity.text = "Quantities: ${cartItem.selectedQuantity}"
        holder.productSize.text = "Size: ${cartItem.size}"
        holder.productColor.text = "Color: ${cartItem.color}"
    }

    override fun getItemCount(): Int = cartItems.size
}