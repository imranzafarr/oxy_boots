package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R

class SizeAdapter(
    private val sizes: List<Int>,
    private val onSizeSelected: (Int) -> Unit
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.size_rc_view, parent, false)
        return SizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bind(sizes[position], position)
    }

    override fun getItemCount(): Int = sizes.size

    inner class SizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSize: TextView = itemView.findViewById(R.id.size)

        @SuppressLint("SetTextI18n")
        fun bind(size: Int, position: Int) {
            tvSize.text = size.toString()
            tvSize.isSelected = position == selectedPosition

            tvSize.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)
                onSizeSelected(size)
            }
        }
    }
}
