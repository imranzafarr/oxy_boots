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

// Adapter for displaying a list of shoe properties in a RecyclerView
class ShoesRcViewAdapter(private val shoes: List<ShoesItems>) : RecyclerView.Adapter<ShoesRcViewAdapter.ShoeViewHolder>() {

    // ViewHolder class that holds the view bindings for each shoe item
    class ShoeViewHolder(val binding: ShoesRcViewBinding) : RecyclerView.ViewHolder(binding.root)

    // Called when a new ViewHolder is created. Inflates the layout using ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeViewHolder {

        // Inflate the layout for an individual shoe item using ViewBinding
        val binding = ShoesRcViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoeViewHolder(binding) // Return the ViewHolder with the binding
    }

    // Binds the data (shoe item) to the views in the ViewHolder for the specific position
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShoeViewHolder, position: Int) {
        val shoe = shoes[position]  // Get the shoe item for the current position
        val context = holder.binding.root.context // Get the context from the ViewHolder

        // Using Glide to load the shoe image into the ImageView with center-cropped scaling
        Glide.with(context).load(shoe.image).apply(RequestOptions().centerCrop())
            .into(holder.binding.shoeImage)

        // Set the shoe name into the TextView
        holder.binding.shoeName.text = shoe.name

        // Set the price of the shoe in the format "Rs. <price>"
        holder.binding.shoePrice.text = "Rs. ${shoe.price}"

        // Handle the click event on the root view (shoe item) to open the ShoeDetailsActivity
        holder.binding.root.setOnClickListener{

            // Create an intent to start the ShoeDetailsActivity
            val intent = Intent(context, ShoeDetailsActivity::class.java).apply {

                // Pass shoe details to the next activity via intent extras
                putExtra("id",shoe.id)
                putExtra("image",shoe.image)
                putExtra("name",shoe.name)
                putExtra("description",shoe.description)
                putExtra("price",shoe.price)
                putExtra("quantity",shoe.quantity)
            }
            context.startActivity(intent)  // Start the ShoeDetailsActivity
        }

    }
    // Returns the total number of items (shoes) in the list
    override fun getItemCount(): Int = shoes.size
}



