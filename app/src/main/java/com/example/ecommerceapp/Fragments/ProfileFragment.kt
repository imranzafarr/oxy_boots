package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Models.UserDetails
import com.example.ecommerceapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    //Applying binding
    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }
    //Getting instance of Firebase FireStore
    private val db = FirebaseFirestore.getInstance()

    // Firebase Auth instance
    private val auth = FirebaseAuth.getInstance()
    //Define PICK_IMAGE_REQUEST constant
    private val pickImageRequest = 1
    //Define imageUri variable
    private var imageUri: Uri? = null
    // Keys for SharedPreferences
    private val sharedPrefs = "profile_prefs"
    private val imageUriKey = "profile_image_uri"
    private val userIdKey = "user_id_key"

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

        // Load saved image when fragment is created
        loadSavedImage()
//       Function call to fetch user data
        fetUserData()

        // Handling Profile Image click
        binding.imageButton.setOnClickListener {
            //Function call to open gallery
            openGallery()
        }

    }

    private fun loadSavedImage() {
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val savedUri = sharedPreferences.getString(imageUriKey, null)
        val savedUserId = sharedPreferences.getString(userIdKey, null)
        val currentUserId = auth.currentUser?.uid

        if (savedUri != null && savedUserId == currentUserId) {
            // If the saved UID matches the current user's UID, load the image
            imageUri = Uri.parse(savedUri)
            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(binding.profileImage)
        } else {
            // If UID doesn't match or no saved image, load the placeholder
            binding.profileImage.setImageResource(R.drawable.image) // Replace with your placeholder image
        }
    }

    //Function to open gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, pickImageRequest)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            val currentUserId = auth.currentUser?.uid

            if (currentUserId != null) {
                // Save the image URI and UID to SharedPreferences
                saveImageUri(imageUri.toString(), currentUserId)

                // Load the image using Glide
                Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(binding.profileImage)
            } else {
                // Show placeholder if no user is logged in
                binding.profileImage.setImageResource(R.drawable.image) // Replace with your placeholder image
            }
        }
    }

    // Function to save image uri
    private fun saveImageUri(uri: String, userId: String) {
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(imageUriKey, uri)
        editor.putString(userIdKey, userId)
        editor.apply()
        // Notify StoreActivity that the image has been updated
        val intent = Intent("PROFILE_IMAGE_UPDATED")
        intent.putExtra("IMAGE_URI", uri)
        requireActivity().sendBroadcast(intent)
    }

    //Function to fetch user data
    @SuppressLint("SetTextI18n")
    private fun fetUserData() {
        //Get current user
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid // The logged-in user's UID

            // Fetch user details from FireStore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(UserDetails::class.java) // Map FireStore data to User class
                        user?.let {
                            // Display user data in the TextView
                            binding.profileName.text = it.name
                            binding.profileEmail.text = it.email
                        }
                    } else {
                        binding.profileName.text = "No name!"
                        binding.profileEmail.text = "No email!"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FireStore", "Error fetching user data", exception)

                }
        } else {
            binding.profileEmail.text = "No user is logged in!"
            binding.profileName.text = "No user is logged in!"
        }
            }
    }


