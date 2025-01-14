package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Models.NotificationItems

import com.example.ecommerceapp.R


class NotificationAdapter(private val notifications: List<NotificationItems>) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time)
        val message: TextView = itemView.findViewById(R.id.message)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notifications_rc_view, parent, false)
        return NotificationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        val elapsedTime = calculateElapsedTime(notification.timestamp)
        holder.time.text = elapsedTime.toString()
        holder.message.text = notification.message


    }

    private fun calculateElapsedTime(timestamp: Long): Any {

            val currentTime = System.currentTimeMillis()
            val diff = currentTime - timestamp

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
                hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
                minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
                else -> "Just Now"
            }

    }

    override fun getItemCount(): Int = notifications.size
}
