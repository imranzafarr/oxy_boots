package com.example.ecommerceapp
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.ecommerceapp.databinding.ActivityPasswordRecoveryBinding
import com.google.firebase.auth.FirebaseAuth

class PasswordRecoveryActivity : AppCompatActivity() {

    // Using view binding for efficient and type-safe UI access
    private val passwordRecoveryBinding: ActivityPasswordRecoveryBinding by lazy{
        ActivityPasswordRecoveryBinding.inflate(layoutInflater)
    }

    // FirebaseAuth instance for authentication operations
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the activity layout using view binding
        setContentView(passwordRecoveryBinding.root)


        /* Adjust the padding of the main view to accommodate system insets
        (status bar, navigation bar, etc.)*/
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Authentication instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Set up the back button to navigate to the SignInActivity when clicked
        passwordRecoveryBinding.backImage.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish() // Finish the current activity to remove it from the back stack
        }

        // Set up the continue button click listener to start the password recovery process
        passwordRecoveryBinding.continueButton.setOnClickListener {
            // Get the email entered by the user and remove any leading/trailing whitespace
            val email = passwordRecoveryBinding.emailText.text.toString()

            // Validate the email input: Check if it's empty or not in a valid email format
            if (email.isEmpty()) {
                // Show error dialog prompting the user to enter an email address
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Please Enter Your Email")
                    .setConfirmText("OK")
                    .show()
            }

            else{
                // If the email is valid, attempt to send a password reset email using Firebase
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        // On success, navigate back to the SignInActivity
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        // On failure, display an error dialog
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Something Went Wrong")
                            .setConfirmText("OK")
                            .show()
                    }
            }
        }
    }
}