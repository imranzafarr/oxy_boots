package com.example.ecommerceapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Models.OnBoardingItems
import com.example.ecommerceapp.R



class OnBoardingItemsAdapter(private val onBoardingItems: List<OnBoardingItems>)
    :RecyclerView.Adapter<OnBoardingItemsAdapter.OnBoardingItemViewHolder>(){
    inner class OnBoardingItemViewHolder(view:View):RecyclerView.ViewHolder(view){
        private val onBoardingImage=view.findViewById<ImageView>(R.id.onBoardingImage)
        val extraImage=view.findViewById<ImageView>(R.id.extraImage)
        private val textTitle=view.findViewById<TextView>(R.id.onBoardingTitle)
        private val textDescription=view.findViewById<TextView>(R.id.onBoardingDescription)

        fun bind(onBoardingItems: OnBoardingItems){
            onBoardingImage.setImageResource(onBoardingItems.image)
            onBoardingItems.extraShoe?.let { extraImage.setImageResource(it) }
            textTitle.text=onBoardingItems.title
            textDescription.text=onBoardingItems.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingItemViewHolder {
        return OnBoardingItemViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.onboarding_items,parent,false
        ))
    }

    override fun getItemCount(): Int {
        return onBoardingItems.size
    }

    override fun onBindViewHolder(holder: OnBoardingItemViewHolder, position: Int) {
        holder.bind(onBoardingItems[position])
        // If the position is 1 (item 2), make the second image visible
        if (position == 1) {
            holder.extraImage.visibility = View.VISIBLE
            holder.extraImage.setImageResource(R.drawable.extrashoe)
        } else {
            holder.extraImage.visibility = View.GONE
        }
    }
}

