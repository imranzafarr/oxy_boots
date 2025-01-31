package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.Adapters.OrderAdapter
import com.example.ecommerceapp.Models.OrderDetails
import com.example.ecommerceapp.databinding.FragmentOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class OrderFragment : Fragment() {

    private val binding by lazy { FragmentOrderBinding.inflate(layoutInflater)}
    //Defining OrderAdapter
    private lateinit var orderAdapter: OrderAdapter
    //Defining orderItems
    private val orderItems=ArrayList<OrderDetails>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        //Setting up RecyclerView
        orderAdapter= OrderAdapter(orderItems)
        binding.orderRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            adapter=orderAdapter

        }
        //Function call to fetch order details
        fetchOrderItems()

    }
    // Function to fetch order details from FireStore
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchOrderItems() {
        //Getting current user id
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        // Checking if user is logged in
        if (currentUserUid != null) {
            //Getting FireStore instance
            val firestore = FirebaseFirestore.getInstance()
            //Getting order details from FireStore
            firestore.collection("orderDetails")
                .whereEqualTo("uid", currentUserUid)
                .get()
                //Handling success
                .addOnSuccessListener{querySnapshot->
                    orderItems.clear()
                    for (document in querySnapshot) {
                        val item = document.toObject(OrderDetails::class.java)
                        orderItems.add(item)
                    }
                    orderAdapter.notifyDataSetChanged()
                    toggleEmptyState()
                }
                //Handling failure
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to fetch Order Details", Toast.LENGTH_SHORT).show()
                }

        }
        //Generating a toast if user is not logged in
        else{
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check if the order list is empty
    private fun toggleEmptyState() {
        if (orderItems.isEmpty()) {
            binding.emptytext.visibility = View.VISIBLE
            binding.orderRecyclerView.visibility = View.GONE
        } else {
            binding.emptytext.visibility = View.GONE
            binding.orderRecyclerView.visibility = View.VISIBLE
        }
    }

}