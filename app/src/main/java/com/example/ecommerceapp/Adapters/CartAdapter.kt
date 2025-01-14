package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecommerceapp.Models.Cart
import com.example.ecommerceapp.Models.CartItems
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.CartRcViewBinding


import com.google.firebase.firestore.FirebaseFirestore


class CartAdapter(private val shoes: List<Cart>,
                  private val onQuantityChanged: () -> Unit)
    : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder class with binding
    class CartViewHolder(val binding: CartRcViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartRcViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val shoe = shoes[position]
        val context = holder.binding.root.context

        // Using Glide to load the shoe image
        Glide.with(context).load(shoe.image).apply(RequestOptions().centerCrop())
            .into(holder.binding.shoeImage)
        // Binding name
        holder.binding.shoeName.text = shoe.name
        holder.binding.shoeColor.text = shoe.color
        holder.binding.shoeSize.text = "Size: "+shoe.size


        // Initialize quantity and price
        holder.binding.quantity.text = shoe.selectedQuantity.toString()
        holder.binding.shoePrice.text = "Rs. ${shoe.price.toInt() * shoe.selectedQuantity}"



        // Handle increase button
        holder.binding.increaseButton.setOnClickListener {
            if (shoe.selectedQuantity < shoe.quantity.toInt()){
                shoe.selectedQuantity++
                holder.binding.quantity.text = shoe.selectedQuantity.toString()
                holder.binding.shoePrice.text = "Rs. ${shoe.price.toInt() * shoe.selectedQuantity}"
            }
            else{
                Toast.makeText(context, "Only ${shoe.quantity} shoes are available", Toast.LENGTH_SHORT).show()
            }

            onQuantityChanged()
        }



        holder.binding.decreaseButton.setOnClickListener {
            if (shoe.selectedQuantity > 1) {
                shoe.selectedQuantity--
                holder.binding.quantity.text = shoe.selectedQuantity.toString()
                holder.binding.shoePrice.text = "Rs. ${shoe.price.toInt() * shoe.selectedQuantity}"
            }
            else {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show()
            }
                onQuantityChanged()

        }


        // Handling click on delete button
        holder.binding.deleteButton.setOnClickListener {
            //Getting instance of FireStore
            val db = FirebaseFirestore.getInstance()
            //Checking if the shoe is already in cart
            db.collection("cart")
                .whereEqualTo("uid", shoe.uid)
                .whereEqualTo("name", shoe.name)
                .get()
                //Handling success
                .addOnSuccessListener { querySnapshot ->
                    //If the shoe is in cart, remove it
                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id

                        db.collection("cart").document(documentId).delete()
                            .addOnSuccessListener {
//                                Toast.makeText(context, "Removed from Cart", Toast.LENGTH_SHORT)
//                                    .show()
                                (shoes as MutableList).removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, shoes.size)
                                onQuantityChanged()
                            }
                            //Handling failure
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Failed to Remove from Cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    //Generating a toast if the shoe is not in cart
                    else {
                        Toast.makeText(context, "Item not found in Cart", Toast.LENGTH_SHORT).show()
                    }
                }
                //Handling failure
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error accessing Cart: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


    }

    override fun getItemCount(): Int = shoes.size
}



