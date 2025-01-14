package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Adapters.ColorAdapter
import com.example.ecommerceapp.Adapters.SizeAdapter
import com.example.ecommerceapp.Models.CartItems
import com.example.ecommerceapp.Models.ColorItems
import com.example.ecommerceapp.Models.FavoriteItems
import com.example.ecommerceapp.Models.ShoesItems
import com.example.ecommerceapp.databinding.ActivityShoeDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ShoeDetailsActivity : AppCompatActivity() {
    //Applying binding
    private val binding: ActivityShoeDetailsBinding by lazy {
        ActivityShoeDetailsBinding.inflate(layoutInflater)
    }

    //Initializing color and size
    private var color: String? = null
    private var size: String? = null

    //Getting Firebase instance
    private val db = FirebaseFirestore.getInstance()
    //Getting current user id
    private val currentUserid = FirebaseAuth.getInstance().currentUser?.uid

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // back Image click listener
        binding.backImage.setOnClickListener {
            finish()
        }

        // Getting the Shoe details from the intent
//        val shoe = intent.getSerializableExtra("shoe") as ShoesItems
        val shoeImage = intent.getStringExtra("image")
        val shoeName = intent.getStringExtra("name")
        val shoeDescription = intent.getStringExtra("description")
        val shoePrice = intent.getStringExtra("price")
        val quantity = intent.getStringExtra("quantity")

        //Setting the shoe details
        binding.name.text = shoeName
        binding.description.text = shoeDescription
        binding.shoePrice.text = "Rs. $shoePrice"
        Glide.with(this).load(shoeImage).into(binding.image)
        binding.quantity.text = quantity

        // Defining a list of sizes
        val sizes = listOf(6, 7, 8, 9, 10, 11, 12)

       // Setting up the RecyclerView for sizes
        binding.sizeRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.sizeRecyclerView.adapter = SizeAdapter(sizes) { selectedSize ->
            size = selectedSize.toString()
//            Toast.makeText(this, "Selected Size: $size", Toast.LENGTH_SHORT).show()
        }

        // Defining a list of colors (using ARGB values)
        val colors = listOf(
            ColorItems(Color.BLUE, "Blue"),
            ColorItems(Color.GREEN, "Green"),
            ColorItems(Color.YELLOW, "Yellow"),
            ColorItems(Color.BLACK, "Black"),
            ColorItems(Color.MAGENTA, "Pink"),
            ColorItems(Color.RED, "Red"),
            ColorItems(Color.WHITE, "White")
        )

        // Setting up the RecyclerView for colors
        binding.colorRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.colorRecyclerView.adapter = ColorAdapter(colors) { selectedColorName ->
            color = selectedColorName
//            Toast.makeText(this, "Selected Color: $color", Toast.LENGTH_SHORT).show()
        }

        //Handling click on favorite icon
        binding.heartImage.setOnCheckedChangeListener { _, isChecked ->
            //Checking if user is not logged in
            if (currentUserid == null) {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
                return@setOnCheckedChangeListener
            }

            //Getting a reference to the "favorites" collection
            val favoriteRef = db.collection("favorites")
                .whereEqualTo("uid", currentUserid)
                .whereEqualTo("name", shoeName) // Check by shoe name or unique identifier

            //Checking if the shoe is already in favorites
            favoriteRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Shoe is already favorite
                        val documentId = querySnapshot.documents[0].id
                        if (isChecked) {
                            Toast.makeText(this, "Already added to Favorites", Toast.LENGTH_SHORT).show()
                            binding.heartImage.isChecked = true
                        } else {
                            // Removing from favorites
                            db.collection("favorites").document(documentId).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to Remove from Favorites", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        if (isChecked) {
                            // Adding to favorites
                            val favoriteItems = FavoriteItems(
                                uid = currentUserid,
                                name = shoeName.toString(),
                                price = shoePrice.toString(),
                                image = shoeImage.toString(),
                                description = shoeDescription.toString(),
                                quantity = quantity.toString()
                            )

                            db.collection("favorites").add(favoriteItems)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to Add to Favorites", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error checking Favorites: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        //Handling click on cart button
        binding.cartButton.setOnClickListener {

            //Checking if size and color are selected
            if(size==null || color==null) {
                Toast.makeText(this, "Please select both size and color", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Checking if the user is logged in
            if (currentUserid == null) {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Reference to "cart" collection
            val cartRef = db.collection("cart")
                .whereEqualTo("uid", currentUserid)
                .whereEqualTo("name", shoeName)

            // Checking if the shoe is already in the cart
            cartRef.get()
                .addOnSuccessListener { querySnapshot ->
//                    if (!querySnapshot.isEmpty) {
//                        // Shoe is already in the cart
//                        Toast.makeText(this, "Already in cart", Toast.LENGTH_SHORT).show()
//                    } else {
                        // Adding the shoe to the cart
                        val cartItem = CartItems(
                            uid = currentUserid,
                            image = shoeImage.toString(),
                            name = shoeName.toString(),
                            description = shoeDescription.toString(),
                            quantity = quantity.toString(),
                            price = shoePrice.toString(),
                            size = size.toString(),
                            color = color.toString()
                        )

                        db.collection("cart").add(cartItem)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()

                                // Navigate to MainActivity and then CartFragment
                                val intent = Intent(this, StoreActivity::class.java).apply {
                                    putExtra("navigateTo", "cartFragment")
                                }
                                startActivity(intent)

                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to add to cart: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
//                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error checking cart: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        }

        }
        }


