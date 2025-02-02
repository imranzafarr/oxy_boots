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

class OrderAdapter(private val orderList: List<OrderDetails>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        val expandButton: ImageView = itemView.findViewById(R.id.expandButton)
        val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_rc_view, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
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

        holder.itemsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemsRecyclerView.adapter = ItemsAdapter(order.cartItems)

        // Handle expand/collapse
        var isExpanded = false
        holder.expandButton.setOnClickListener {
            isExpanded = !isExpanded
            holder.itemsRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.expandButton.rotation = if (isExpanded) 180f else 0f
        }

    }

    override fun getItemCount(): Int = orderList.size
}
