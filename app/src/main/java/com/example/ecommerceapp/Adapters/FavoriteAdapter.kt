package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecommerceapp.Models.FavoriteItems
import com.example.ecommerceapp.ShoeDetailsActivity
import com.example.ecommerceapp.databinding.FavoriteRcViewBinding

import com.google.firebase.firestore.FirebaseFirestore

// Adapter for displaying a list of favorite shoes in a RecyclerView
class FavoriteAdapter(private val shoes: List<FavoriteItems>, // List of favorite shoes
 private val onItemRemoved: () -> Unit) // Callback function to handle item removal from favorites
    : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    // ViewHolder class for holding views using ViewBinding
    class FavoriteViewHolder(val binding: FavoriteRcViewBinding) : RecyclerView.ViewHolder(binding.root)

    // Creates new ViewHolder instances when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavoriteRcViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val shoe = shoes[position] // Get the shoe item at the current position
        val context = holder.binding.root.context  // Get the context

        // Load the shoe image using Glide with center crop option
        Glide.with(context).load(shoe.image).apply(RequestOptions().centerCrop())
            .into(holder.binding.shoeImage)

        // Bind shoe details to the respective UI components
        holder.binding.shoeName.text = shoe.name
        // Binding price
        holder.binding.shoePrice.text = shoe.description
        // Binding price
        holder.binding.shoePrice.text = "Rs. ${shoe.price}"

        // Handle favorite icon click to remove the item from favorites
        holder.binding.favoriteIcon.setOnClickListener{

            //Getting instance of FireStore
            val db = FirebaseFirestore.getInstance()

            // Check if the selected shoe is already in the "favorites" collection
            db.collection("favorites")
                .whereEqualTo("uid", shoe.uid)  // Match by user ID
                .whereEqualTo("name", shoe.name) // Match by shoe name
                .get()
                //Handling success
                .addOnSuccessListener { querySnapshot ->
                    // If the shoe exists in favorites, remove it
                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id

                        // Delete the favorite item from Firestore
                        db.collection("favorites").document(documentId).delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Removed from Favorites",
                                    Toast.LENGTH_SHORT).show()

                                // Update RecyclerView by removing the item
                                (shoes as MutableList).removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, shoes.size)

                                // Invoke callback to notify item removal
                                onItemRemoved()
                            }
                            //Handling failure
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to Remove from Favorites", Toast.LENGTH_SHORT).show()
                            }
                    }
                    //Show a a toast message if the shoe is not in favorites
                    else {
                        Toast.makeText(context, "Item not found in Favorites", Toast.LENGTH_SHORT).show()
                    }
                }
                //Handling failure
                .addOnFailureListener {
                    Toast.makeText(context, "Error accessing Favorites: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        //Handling click on shoe item
        holder.binding.root.setOnClickListener {
            // Create an intent to open ShoeDetailsActivity and pass shoe details
            val intent = Intent(context, ShoeDetailsActivity::class.java).apply {
                putExtra("image",shoe.image)
                putExtra("name",shoe.name)
                putExtra("description",shoe.description)
                putExtra("price",shoe.price)
                putExtra("quantity",shoe.quantity)

            }
            context.startActivity(intent)  // Start the activity with shoe details
        }
    }

    // Returns the total number of favorite items
    override fun getItemCount(): Int = shoes.size
}
