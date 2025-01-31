package com.example.ecommerceapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Models.ColorItems
import com.example.ecommerceapp.R
class ColorAdapter(
    private val colors: List<ColorItems>,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.color_rc_view, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], position)
    }

    override fun getItemCount(): Int = colors.size

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vColor: View = itemView.findViewById(R.id.vColor)
        private val vSelected: View = itemView.findViewById(R.id.vSelected)

        fun bind(colorItem: ColorItems, position: Int) {
            // If color is a drawable, set it as background
            if (colorItem.color.toString().startsWith("R.drawable")) {
                vColor.setBackgroundResource(colorItem.color)
            } else {
                vColor.setBackgroundColor(colorItem.color)
            }

            vSelected.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)
                onColorSelected(colorItem.name)
            }
        }
    }
}


