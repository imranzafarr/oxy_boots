package com.example.ecommerceapp
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecommerceapp.Models.Cart
import com.example.ecommerceapp.Models.OrderDetails
import com.example.ecommerceapp.databinding.ActivityCheckoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CheckoutActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityCheckoutBinding.inflate(layoutInflater) }
    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    //Getting Firebase instance
    private val db = FirebaseFirestore.getInstance()

    //Getting current user id
    private val currentUserid = FirebaseAuth.getInstance().currentUser?.uid

    @SuppressLint("SetTextI18n")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Navigating to previous activity
        binding.backImage.setOnClickListener {
            this@CheckoutActivity.onBackPressedDispatcher.addCallback(this, object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
            onBackPressedDispatcher.onBackPressed()
        }

        // Initializing location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        // Initializing map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Setting up current location button
        binding.locationButton.setOnClickListener {
            getCurrentLocation()
        }

//       Function call to fetch user details for second time order
        fetchUserDetails()

        //Getting data from previous activity
        val subTotal = intent.getIntExtra("subTotal", 0)
        val tax = intent.getIntExtra("tax", 0)
        val totalCost = intent.getIntExtra("totalCost", 0)
        val cartItems = intent.getParcelableArrayListExtra<Cart>("cartItems") ?: ArrayList()
        val items = intent.getIntExtra("items", 0)
        binding.subtotal.text = "Rs. $subTotal"
        binding.shipping.text = "Rs. $tax"
        binding.totalCost.text = "Rs. $totalCost"

        // Handling payment button logic
        binding.paymentButton.setOnClickListener {
            // Setting fields names
            val name = binding.nameText.text.toString()
            val email = binding.emailText.text.toString()
            val phone = binding.phoneText.text.toString()
            val address = binding.addressText.text.toString()
            val paymentMethod = "Cash on Delivery"

            // Checking if any field is empty
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                // Showing error message if any field is empty
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }

            // Checking if the user is logged in
            if (currentUserid == null) {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Generating order id
            val orderId = System.currentTimeMillis()
            // Generating order date and time
            val orderDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
            // Parse the orderDate to a Date object
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val parsedOrderDate = dateFormat.parse(orderDate)

            // Adding one day to the orderDate
            val calendar = Calendar.getInstance()
            calendar.time = parsedOrderDate
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val deliveryDate = dateFormat.format(calendar.time)

//            val orderTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            // Creating order object
            val order = OrderDetails(
                orderId = orderId,
                uid = currentUserid,
                name = name, email = email,
                phone = phone,
                address = address,
                subTotal = subTotal,
                shippingFee = tax,
                totalCost = totalCost,
                items = items.toString(),
                paymentMethod = paymentMethod,
                orderDate = orderDate,
                deliveryDate = deliveryDate,
                cartItems = cartItems
            )

            // Saving order to fireStore database
            saveOrderDetails(order)

            // Clearing the cart
            clearCart()
        }

    }

    // Function to handle map ready event
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Enabling zoom controls
        map.uiSettings.isZoomControlsEnabled = true
        // Setting up map click listener
        map.setOnMapClickListener { latLng ->
            updateLocationOnMap(latLng)
        }
        // Requesting location permission if not granted
        if (checkLocationPermission()) {
            getCurrentLocation()
        }
    }

    // Function to check location permission
    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return false
        }
        return true
    }

    // Function for getting current location
    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(location.latitude, location.longitude)
                    updateLocationOnMap(latLng)
                }
            }
        }
    }

    // Function to update location on map
    private fun updateLocationOnMap(latLng: LatLng) {
        // Clear previous markers
        map.clear()

        // Adding marker at selected location
        map.addMarker(MarkerOptions().position(latLng))

        // Moving camera to location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        // Updating address EditText
        getAddressFromLocation(latLng)
    }

    // Function to get address from location
    private fun getAddressFromLocation(latLng: LatLng) {
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val addressText = address.getAddressLine(0)
                binding.addressText.setText(addressText)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show()
        }
    }
    // Function to handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    // Function to save order details
    private fun saveOrderDetails(order: OrderDetails) {
        db.collection("orderDetails")
            .add(order)
            .addOnSuccessListener {
                sendOrderNotification(order)
                startActivity(Intent(this, PaymentSuccessActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("NotificationPermission")
    // Function to send notification
    private fun sendOrderNotification(order: OrderDetails) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "order_channel"
        val channelName = "Order Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationTitle = "Order Placed Successfully"
        val notificationMessage = "Your order (ID: ${order.orderId}) has been placed."
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notificationtwo)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        notificationManager.notify(order.orderId.toInt(), notificationBuilder.build())

    }

    // Function to fetch user details for second time order
    private fun fetchUserDetails() {
        if (currentUserid == null) return

        // Query the latest order by the current user
        db.collection("orderDetails")
            .whereEqualTo("uid", currentUserid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val address = document.getString("address") ?: ""

                    // Pre-fills the fields
                    binding.nameText.setText(name)
                    binding.emailText.setText(email)
                    binding.phoneText.setText(phone)
                    binding.addressText.setText(address)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch user details", Toast.LENGTH_SHORT).show()
            }
    }

    //Function to clear cart items
    private fun clearCart() {
        // Getting FireStore instance
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            db.collection("cart")
                .whereEqualTo("uid", currentUserUid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // Deleting each item from the cart collection
                        document.reference.delete()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to clear cart", Toast.LENGTH_SHORT).show()
                }
        }
    }
}