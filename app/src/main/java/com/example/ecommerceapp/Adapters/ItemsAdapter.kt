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

// Adapter for displaying a list of cart items in a RecyclerView
class ItemsAdapter(private val cartItems: List<Cart>) :
    RecyclerView.Adapter<ItemsAdapter.CartItemViewHolder>() {

    // ViewHolder class to hold references to the views in each item of the RecyclerView
    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.shoeImage) // Image of the shoes
        val productName: TextView = itemView.findViewById(R.id.shoeName) // Name of the shoes
        val productQuantity: TextView = itemView.findViewById(R.id.quantity) // Quantity of  shoes
        val productSize: TextView = itemView.findViewById(R.id.shoeSize) // Size of the shoes
        val productColor: TextView = itemView.findViewById(R.id.shoeColor)  // Color of the shoes
    }

    // Creates new ViewHolder instances when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        // Inflating the layout for a single cart item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_details, parent, false)
        return CartItemViewHolder(view)  // Returning a new ViewHolder instance
    }

    // Binds data to the ViewHolder based on the position in the list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]  // Getting the current cart item

        // Loading product image using Glide
         Glide.with(holder.itemView.context).load(cartItem.image).into(holder.productImage)

        // Setting text values for shoe details
        holder.productName.text = cartItem.name
        holder.productQuantity.text = "Quantities: ${cartItem.selectedQuantity}"
        holder.productSize.text = "Size: ${cartItem.size}"
        holder.productColor.text = "Color: ${cartItem.color}"
    }

    // Returns the total number of items in the cart
    override fun getItemCount(): Int = cartItems.size
}