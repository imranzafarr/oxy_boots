package com.example.ecommerceapp.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.Adapters.CartAdapter
import com.example.ecommerceapp.CheckoutActivity
import com.example.ecommerceapp.Models.Cart
import com.example.ecommerceapp.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CartFragment : Fragment() {
    private val binding by lazy { FragmentCartBinding.inflate(layoutInflater)}
    //Defining CartAdapter
    private lateinit var cartAdapter: CartAdapter
    //Defining cartItems
    private val cartItems=ArrayList<Cart>()

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
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //backImage click effect
        binding.backImage.setOnClickListener {
            requireActivity().onBackPressed()

        }



        //Setting up RecyclerView
        cartAdapter= CartAdapter(cartItems){ updateCosts() }
        binding.cartRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            adapter=cartAdapter

        }
        //Function call to fetch Cart items
        fetchCartItems()

        binding.checkoutButton.setOnClickListener {
            var subTotal=0
            var tax=0
            for (item in cartItems){
                subTotal+=item.price.toInt()*item.selectedQuantity
                tax=subTotal*1/100
            }
            val totalCost=subTotal+tax
            // Adding number of items to the intent
            val items = cartItems.size
            val intent= Intent(requireContext(), CheckoutActivity::class.java).apply{
            putExtra("subTotal",subTotal)
            putExtra("tax",tax)
            putExtra("totalCost",totalCost)
                putExtra("items",items)
                putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
        }
            startActivity(intent)
        }

        }

    @SuppressLint("SetTextI18n")
    private fun updateCosts() {
        var subtotal = 0
        var tax=0

        // Calculate subtotal based on user-selected quantities
        for (item in cartItems) {
            subtotal += item.price.toInt() * item.selectedQuantity
            tax=subtotal*1/100
        }

        // Update subtotal and total cost TextViews
        binding.subtotal.text = "Rs. $subtotal"
        binding.shipping.text = "Rs. $tax"
        val totalCost = subtotal + tax
        binding.totalCost.text = "Rs. $totalCost"

        // Showing empty text if cart is empty
        if (cartItems.isEmpty()) {
            binding.empty.visibility = View.VISIBLE
            binding.cartRecyclerView.visibility = View.GONE
            binding.subTotalTextView.visibility = View.GONE
            binding.shipping.visibility = View.GONE
            binding.shippingTextView.visibility = View.GONE
            binding.totalCostTextView.visibility = View.GONE
            binding.subtotal.visibility = View.GONE
            binding.totalCost.visibility = View.GONE
            binding.checkoutButton.visibility = View.GONE
        } else {
            binding.empty.visibility = View.GONE
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.subTotalTextView.visibility = View.VISIBLE
            binding.shipping.visibility = View.VISIBLE
            binding.shippingTextView.visibility = View.VISIBLE
            binding.totalCostTextView.visibility = View.VISIBLE
            binding.subtotal.visibility = View.VISIBLE
            binding.totalCost.visibility = View.VISIBLE
            binding.checkoutButton.visibility = View.VISIBLE
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCartItems() {
        //Getting current user id
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        // Checking if user is logged in
        if (currentUserUid != null) {
            //Getting FireStore instance
            val firestore = FirebaseFirestore.getInstance()
            //Getting cart items from FireStore
            firestore.collection("cart")
                .whereEqualTo("uid", currentUserUid)
                .get()
                //Handling success
                .addOnSuccessListener{querySnapshot->
                    cartItems.clear()
                    for (document in querySnapshot) {
                        val item = document.toObject(Cart::class.java)
                        cartItems.add(item)
                    }
                    cartAdapter.notifyDataSetChanged()
                    updateCosts()
                }
                //Handling failure
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to fetch Cart Items", Toast.LENGTH_SHORT).show()
                }

        }
        //Generating a toast if user is not logged in
        else{
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
