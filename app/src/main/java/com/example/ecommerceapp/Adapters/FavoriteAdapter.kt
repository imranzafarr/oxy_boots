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


class FavoriteAdapter(private val shoes: List<FavoriteItems>,
                      private val onItemRemoved: () -> Unit)
    : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    // ViewHolder class with binding
    class FavoriteViewHolder(val binding: FavoriteRcViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavoriteRcViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val shoe = shoes[position]
        val context = holder.binding.root.context

        // Using Glide to load the shoe image
        Glide.with(context).load(shoe.image).apply(RequestOptions().centerCrop())
            .into(holder.binding.shoeImage)
        // Binding name
        holder.binding.shoeName.text = shoe.name
        // Binding price
        holder.binding.shoePrice.text = shoe.description
        // Binding price
        holder.binding.shoePrice.text = "Rs. ${shoe.price}"

        // Handling click on favorite icon
        holder.binding.favoriteIcon.setOnClickListener{
            //Getting instance of FireStore
            val db = FirebaseFirestore.getInstance()
            //Checking if the shoe is already in favorites
            db.collection("favorites")
                .whereEqualTo("uid", shoe.uid)
                .whereEqualTo("name", shoe.name)
                .get()
                //Handling success
                .addOnSuccessListener { querySnapshot ->
                    //If the shoe is in favorites, remove it
                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id

                        db.collection("favorites").document(documentId).delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                                (shoes as MutableList).removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, shoes.size)
                                onItemRemoved()
                            }
                            //Handling failure
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to Remove from Favorites", Toast.LENGTH_SHORT).show()
                            }
                    }
                    //Generating a toast if the shoe is not in favorites
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
            //Passing shoe details to ShoeDetailsActivity
            val intent = Intent(context, ShoeDetailsActivity::class.java).apply {
                putExtra("image",shoe.image)
                putExtra("name",shoe.name)
                putExtra("description",shoe.description)
                putExtra("price",shoe.price)
                putExtra("quantity",shoe.quantity)

            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = shoes.size
}
