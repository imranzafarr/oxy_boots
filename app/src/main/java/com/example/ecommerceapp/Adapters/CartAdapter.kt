package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecommerceapp.Models.Cart
import com.example.ecommerceapp.databinding.CartRcViewBinding
import com.google.firebase.firestore.FirebaseFirestore

// Adapter for displaying a list of shoes in the cart and handling quantity changes and removal
class CartAdapter(private val shoes: List<Cart>,
                  private val onQuantityChanged: () -> Unit)
    : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder class with binding for the cart item
    class CartViewHolder(val binding: CartRcViewBinding) : RecyclerView.ViewHolder(binding.root)

    // Called when a new ViewHolder is created. Inflates the cart item layout using ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartRcViewBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return CartViewHolder(binding) // Return the ViewHolder with the binding
    }

    // Binds the shoe data (cart item) to the views in the ViewHolder for the specific position
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val shoe = shoes[position] // Get the shoe item for the current position
        val context = holder.binding.root.context  // Get the context from the ViewHolder

        // Using Glide to load the shoe image into the ImageView with center-cropped scaling
        Glide.with(context).load(shoe.image).apply(RequestOptions().centerCrop())
            .into(holder.binding.shoeImage)

        // Binding the shoe name, color, and size to respective TextViews
        holder.binding.shoeName.text = shoe.name
        holder.binding.shoeColor.text = shoe.color
        holder.binding.shoeSize.text = "Size: "+shoe.size

        // Initializing quantity and price
        holder.binding.quantity.text = shoe.selectedQuantity.toString()
        holder.binding.shoePrice.text = "Rs. ${shoe.price.toInt() * shoe.selectedQuantity}"

        // Handling the increase button click to increase the quantity of the shoe
        holder.binding.increaseButton.setOnClickListener {

            // Check if the selected quantity is less than the available stock
            if (shoe.selectedQuantity < shoe.quantity.toInt()){
                shoe.selectedQuantity++ // Increment the quantity
                holder.binding.quantity.text = shoe.selectedQuantity.toString() // Update the displayed quantity

                // Update the price
                holder.binding.shoePrice.text = "Rs. ${shoe.price.toInt() * shoe.selectedQuantity}"
            }
            else{
                // Show a message if there are not enough shoes in stock
                Toast.makeText(context, "Only ${shoe.quantity} shoes are available", Toast.LENGTH_SHORT).show()
            }

            // Notify that the quantity has changed
            onQuantityChanged()
        }

        // Handling the decrease button click to decrease the quantity of the shoe
        holder.binding.decreaseButton.setOnClickListener {

            // Check if the quantity is greater than 1 to avoid negative quantities
            if (shoe.selectedQuantity > 1) {
                shoe.selectedQuantity--  // Decrease the quantity

                // Update the displayed quantity
                holder.binding.quantity.text = shoe.selectedQuantity.toString()

                // Update the price
                holder.binding.shoePrice.text = "Rs. ${shoe.price.toInt() * shoe.selectedQuantity}"
            }

            else {
                // Show a message if the quantity is already 1 and cannot be decreased further
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show()
            }
               // Notify that the quantity has changed
                onQuantityChanged()

        }

        // Handling the delete button click to remove the shoe from the cart
        holder.binding.deleteButton.setOnClickListener {

            // Get an instance of Firebase FireStore
            val db = FirebaseFirestore.getInstance()

            // Check if the shoe is already in the cart by querying FireStore
            db.collection("cart")
                .whereEqualTo("uid", shoe.uid)
                .whereEqualTo("name", shoe.name)
                .get()

                // Handling success response
                .addOnSuccessListener { querySnapshot ->

                    // If the shoe is in the cart, remove it
                    if (!querySnapshot.isEmpty) {

                        // Get the document ID of the shoe in FireStore
                        val documentId = querySnapshot.documents[0].id

                        // Remove the shoe from the cart in FireStore
                        db.collection("cart").document(documentId).delete()
                            .addOnSuccessListener {

                                // Remove the shoe from the local list and notify the adapter
                                (shoes as MutableList).removeAt(position)

                                // Notify the adapter about the item removal
                                notifyItemRemoved(position)

                                // Update the list to reflect the change
                                notifyItemRangeChanged(position, shoes.size)
                                onQuantityChanged()  // Notify that the quantity has changed
                            }
                            // Handling failure in deletion
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Failed to Remove from Cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                    // Show a message if the shoe is not found in the cart
                    else {
                        Toast.makeText(context, "Item not found in Cart", Toast.LENGTH_SHORT).show()
                    }
                }
                // Handling failure in FireStore query
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error accessing Cart: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    // Returns the total number of items (shoes) in the cart
    override fun getItemCount(): Int = shoes.size
}



