package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R

// Adapter for displaying a list of shoe sizes in a RecyclerView
class SizeAdapter(
    private val sizes: List<Int>,  // List of available sizes to display
    private val onSizeSelected: (Int) -> Unit // Callback function when a size is selected
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    private var selectedPosition = -1  // Keeps track of the currently selected size position

    // Called when a new ViewHolder is created. Inflates the layout for a size item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {

        // Inflate the layout for an individual size item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.size_rc_view, parent,
            false)
        return SizeViewHolder(view)
    }

    // Binds the data (size) to the views in the ViewHolder for the specific position
    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bind(sizes[position], position)
    }

    // Returns the total number of items (sizes) in the list
    override fun getItemCount(): Int = sizes.size

    // ViewHolder class to hold the reference to the size TextView and manage size selection
    inner class SizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Define the TextView that shows the size
        private val tvSize: TextView = itemView.findViewById(R.id.size)

        // Function to bind the size and manage selection state
        @SuppressLint("SetTextI18n")
        fun bind(size: Int, position: Int) {

            // Set the size text
            tvSize.text = size.toString()

            // Highlight the selected size (change its selection state)
            tvSize.isSelected = position == selectedPosition

            // Set an onClickListener to update the selection when a size is clicked
            tvSize.setOnClickListener {
                val previousPosition = selectedPosition // Store the previously selected position
                selectedPosition = position // Update the selected position to the current one

                // Notify the adapter that the previous and new positions need to be updated
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)

                // Invoke the callback function to notify the selected size
                onSizeSelected(size)
            }
        }
    }
}
