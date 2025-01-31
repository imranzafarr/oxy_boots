package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecommerceapp.Models.ShoesItems
import com.example.ecommerceapp.ShoeDetailsActivity
import com.example.ecommerceapp.databinding.ShoesRcViewBinding

class ShoesRcViewAdapter(private val shoes: List<ShoesItems>) : RecyclerView.Adapter<ShoesRcViewAdapter.ShoeViewHolder>() {

    class ShoeViewHolder(val binding: ShoesRcViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeViewHolder {
        val binding = ShoesRcViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoeViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShoeViewHolder, position: Int) {
        val shoe = shoes[position]
        val context = holder.binding.root.context

        // Using Glide to load the shoe image
        Glide.with(context).load(shoe.image).apply(RequestOptions().centerCrop()).into(holder.binding.shoeImage)
        // Binding name
        holder.binding.shoeName.text = shoe.name

        // Binding price
        holder.binding.shoePrice.text = "Rs. ${shoe.price}"

        //Handling click on shoe item
        holder.binding.root.setOnClickListener{
            //Passing shoe details to ShoeDetailsActivity
            val intent = Intent(context, ShoeDetailsActivity::class.java).apply {

                putExtra("id",shoe.id)
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



