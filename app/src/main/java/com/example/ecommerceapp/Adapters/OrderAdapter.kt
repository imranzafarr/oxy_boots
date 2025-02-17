package com.example.ecommerceapp.Adapters

import android.annotation.SuppressLint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Models.OrderDetails
import com.example.ecommerceapp.R

// Adapter for displaying a list of order details in a RecyclerView
class OrderAdapter(private val orderList: List<OrderDetails>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // ViewHolder class to hold the references to each view in the order list item
    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Define views for order details like order ID, date, items, and payment method
        val orderId: TextView = itemView.findViewById(R.id.orderId)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val orderTime: TextView = itemView.findViewById(R.id.orderTime)
        val name: TextView = itemView.findViewById(R.id.name)
        val email: TextView = itemView.findViewById(R.id.email)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val address: TextView = itemView.findViewById(R.id.address)
        val items: TextView = itemView.findViewById(R.id.items)
        val subtotal: TextView = itemView.findViewById(R.id.subtotal)
        val shipping: TextView = itemView.findViewById(R.id.shipping)
        val totalCost: TextView = itemView.findViewById(R.id.totalCost)
        val paymentMethod: TextView = itemView.findViewById(R.id.paymentMethod)

        // Define views for expand button and items RecyclerView (for cart items)
        val expandButton: ImageView = itemView.findViewById(R.id.expandButton)
        val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)
    }

    // Called when a new view holder is created. Inflates the layout for an order item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        // Inflate the layout for an individual order item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_rc_view,
            parent, false)
        return OrderViewHolder(view)
    }

    // Binds the data from the order list to the views in the ViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        // Set text for order details
        val order = orderList[position]
       holder.orderId.text = order.orderId.toString()
        holder.orderDate.text = order.orderDate
        holder.orderTime.text = order.deliveryDate
        holder.name.text = order.name
        holder.email.text = order.email
        holder.phone.text = order.phone
        holder.address.text = order.address
        holder.paymentMethod.text = order.paymentMethod
        holder.items.text = order.items
        holder.subtotal.text = order.subTotal.toString()
        holder.shipping.text = order.shippingFee.toString()
        holder.totalCost.text = order.totalCost.toString()

        // Set up the RecyclerView for cart items (a list of items in the order)
        holder.itemsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemsRecyclerView.adapter = ItemsAdapter(order.cartItems)

        // Handle the expand/collapse logic for the order items list
        var isExpanded = false // Track whether the items list is expanded or collapsed
        holder.expandButton.setOnClickListener {

            isExpanded = !isExpanded  // Toggle the expand/collapse state

            // Show or hide the items RecyclerView based on the state
            holder.itemsRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Rotate the expand button based on the state (180 degrees when expanded)
            holder.expandButton.rotation = if (isExpanded) 180f else 0f
        }

    }

    // Returns the total number of items (orders) in the list
    override fun getItemCount(): Int = orderList.size
}
