package com.example.ecommerceapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Models.ColorItems
import com.example.ecommerceapp.R

// Adapter for displaying a list of color options in a RecyclerView
class ColorAdapter(
    private val colors: List<ColorItems>, // List of color items
    private val onColorSelected: (String) -> Unit // Callback function to handle color selection
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedPosition = -1  // Variable to track the selected color position

    // Creates new ViewHolder instances when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        // Inflating the layout for a single color item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.color_rc_view,
            parent, false)
        return ColorViewHolder(view)
    }

    // Binds data to the ViewHolder based on the position in the list
    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], position)  // Binding color data to the ViewHolder
    }

    // Returns the total number of color items
    override fun getItemCount(): Int = colors.size

    // ViewHolder class to hold references to the views in each color item
    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vColor: View = itemView.findViewById(R.id.vColor) // View representing the color
        private val vSelected: View = itemView.findViewById(R.id.vSelected)  // Indicator for the selected color

        // Function to bind color data to the ViewHolder
        fun bind(colorItem: ColorItems, position: Int) {
            // If color is a drawable, set it as background
            if (colorItem.color.toString().startsWith("R.drawable")) {
                vColor.setBackgroundResource(colorItem.color)
            } else // Otherwise, set the background color directly
            {
                vColor.setBackgroundColor(colorItem.color)
            }


            // Show selection indicator if this item is the selected one
            vSelected.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

            // Set click listener to handle color selection
            itemView.setOnClickListener {
                val previousPosition = selectedPosition // Store previous selected position
                selectedPosition = position // Update selected position
                notifyItemChanged(previousPosition) // Refresh previous selected item
                notifyItemChanged(position)  // Refresh newly selected item
                onColorSelected(colorItem.name)  // Trigger callback with selected color name
            }
        }
    }
}


