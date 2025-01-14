package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager

import com.example.ecommerceapp.Adapters.FavoriteAdapter
import com.example.ecommerceapp.Models.FavoriteItems


import com.example.ecommerceapp.databinding.FragmentFavoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FavoriteFragment : Fragment() {

    //Applying binding
    private val binding by lazy { FragmentFavoriteBinding.inflate(layoutInflater) }
    //Defining FavoriteAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter
    //Defining favoriteItems
    private val favoriteItems=mutableListOf<FavoriteItems>()
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
        //backImage click effect
        binding.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        //Setting up RecyclerView
        favoriteAdapter= FavoriteAdapter(favoriteItems){
            toggleEmptyState()
        }
        binding.favoriteRecyclerView.apply {
            layoutManager= GridLayoutManager(requireContext(),2)
            adapter=favoriteAdapter

        }
        //Function call to fetch favorite items
        fetchFavoriteItems()


    }
    //Function to fetch favorite items
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchFavoriteItems() {
        //Getting current user id
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        // Checking if user is logged in
        if (currentUserUid != null) {
            //Getting FireStore instance
            val firestore = FirebaseFirestore.getInstance()
            //Getting favorite items from FireStore
            firestore.collection("favorites")
                .whereEqualTo("uid", currentUserUid)
                .get()
                //Handling success
                .addOnSuccessListener{querySnapshot->
                    favoriteItems.clear()
                    for (document in querySnapshot) {
                        val item = document.toObject(FavoriteItems::class.java)
                        favoriteItems.add(item)
                    }
                    favoriteAdapter.notifyDataSetChanged()
                    toggleEmptyState()
                }
                //Handling failure
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Failed to fetch Favorite Shoes", Toast.LENGTH_SHORT).show()
                }

        }
        //Generating a toast if user is not logged in
        else{
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEmptyState() {
        if (favoriteItems.isEmpty()) {
            binding.empty.visibility = View.VISIBLE
            binding.favoriteRecyclerView.visibility = View.GONE
        } else {
            binding.empty.visibility = View.GONE
            binding.favoriteRecyclerView.visibility = View.VISIBLE
        }

    }
}