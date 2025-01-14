package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Fragments.CartFragment
import com.example.ecommerceapp.Models.UserDetails
import com.example.ecommerceapp.databinding.ActivityStoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StoreActivity : AppCompatActivity() {
    //Applying binding
    private val binding by lazy { ActivityStoreBinding.inflate(layoutInflater) }

    //Getting instance of Firebase FireStore
    private val firestore = FirebaseFirestore.getInstance()

    //Getting instance of FirebaseAuth
    private val auth = FirebaseAuth.getInstance()

    // Keep track of whether we navigated from drawer
    private var isDrawerNavigation = false
    // Key for SharedPreferences
    private val sharedPrefs = "profile_prefs"
    private val imageUriKey = "profile_image_uri"
    private val userIdKey = "store_user_id"

    @SuppressLint("SetTextI18n")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)
//        Applying insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Setting up NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        // Checking if the activity is launched with a specific navigation request
        val navigateTo = intent.getStringExtra("navigateTo")
        if (navigateTo == "cartFragment") {
            navController.navigate(R.id.cart)
        }

        // Setting up BottomNavigationView with NavController
        binding.bottomNavBar.setupWithNavController(navController)

        // Function call to handle drawer navigation
        setupDrawerNavigation()

        // Back stack changes to handle bottom nav visibility
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                // We're back to the root fragment, show bottom nav
                binding.bottomNavBar.visibility = View.VISIBLE
                isDrawerNavigation = false
            }
        }


        // Getting the header view from NavigationView
        val headerView = binding.navigationView.getHeaderView(0)
        // Finding the TextView in the header view
        val usernameTextView = headerView.findViewById<TextView>(R.id.name)
        // Fetching user data from FireStore
        val currentUserId = auth.currentUser?.uid
        // Checking if user is logged in
        if (currentUserId != null) {
            // Fetching user details from FireStore
            firestore.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    // Checking if the document exists
                    if (documentSnapshot.exists()) {
                        // Converting the document to UserDetails object
                        val userDetails = documentSnapshot.toObject(UserDetails::class.java)
                        // Setting the default username in the TextView
                        val username = userDetails?.name ?: "Guest"
                        // Setting the actual username in the TextView
                        usernameTextView.text = username
                    } else {
                        usernameTextView.text = "Guest"
                    }
                }
                .addOnFailureListener {
                    usernameTextView.text = "Guest"
                }
        }

        // Getting the current user's UID
        if (currentUserId != null) {
            // Retrieving image URI passed from ProfileFragment
            val profileImage = headerView.findViewById<ImageView>(R.id.profileImage)
            val profileImageUri = intent.getStringExtra("PROFILE_IMAGE_URI")

            if (!profileImageUri.isNullOrEmpty()) {
                // Save the image URI and associated user ID in SharedPreferences
                saveImageUri(profileImageUri, currentUserId)

                // Load the image using Glide
                Glide.with(this)
                    .load(Uri.parse(profileImageUri))
                    .circleCrop()
                    .into(profileImage)
            } else {
                // Load saved image URI from SharedPreferences if user IDs match
                val savedUri = loadSavedImageUri(currentUserId)
                if (!savedUri.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(Uri.parse(savedUri))
                        .circleCrop()
                        .into(profileImage)
                } else {
                    // Set default image if no URI is available
                    profileImage.setImageResource(R.drawable.image) // Replace with your placeholder image
                }
            }
        }
    }

    //Function to save image in local storage
    private fun saveImageUri(uri: String,userId: String) {
        val sharedPreferences = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("$imageUriKey-$userId", uri) // Save URI with user ID as part of the key
        editor.putString(userIdKey, userId) // Save the current user ID
        editor.apply()
    }
    // Function to load image URI from SharedPreferences
    private fun loadSavedImageUri(userId: String): String? {
        val sharedPreferences = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        return sharedPreferences.getString("$imageUriKey-$userId", null)
    }

    // Function to handle drawer navigation
    private fun setupDrawerNavigation() {
        // Set item click listener for navigation view
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.profile -> navigateToFragment(ProfileFragment(), R.id.profile)
                R.id.cart -> navigateToFragment(CartFragment(),R.id.cart)
                R.id.favorite -> navigateToFragment(FavoriteFragment(), R.id.favorite)
                R.id.order -> navigateToFragment(OrderFragment(),null)
//                R.id.notification -> navigateToFragment(NotificationsFragment(), null)
                R.id.singOut -> {
                    //Switching to SignIn Activity by clicking on sign out Item
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
            }
            // Close drawer after item is clicked
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true

        }
    }

    // Function to navigate to a fragment
    private fun navigateToFragment(
        fragment: Fragment,
        bottomNavItemId: Int? = null,
        fromDrawer: Boolean = false
    ) {
        isDrawerNavigation = fromDrawer

        binding.bottomNavBar.visibility = if (fromDrawer) View.GONE else View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()

        if (bottomNavItemId != null ) {
            binding.bottomNavBar.selectedItemId = bottomNavItemId

            binding.bottomNavBar.visibility = View.VISIBLE
        } else {
            binding.bottomNavBar.visibility = View.GONE
        }
    }

    // Function to open the drawer
    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    // Handling back navigation
    @Deprecated("This method has been deprecated in favor of using the\n" +
            "      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n" +
            "      The OnBackPressedDispatcher controls how back button events are dispatched\n " +
            "     to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {

        when {
            binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            isDrawerNavigation && supportFragmentManager.backStackEntryCount == 1 -> {
                // If we're about to return to the root fragment from a drawer navigation
                binding.bottomNavBar.visibility = View.VISIBLE
                isDrawerNavigation = false
                super.onBackPressed()
            }

            else -> {
                super.onBackPressed()
            }
        }
    }
    }













