package com.example.ecommerceapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.ecommerceapp.Models.UserDetails
import com.example.ecommerceapp.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private val signUpBinding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(signUpBinding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupClickListeners()
    }

    // Separate functions for clicking effect
    private fun setupClickListeners() {
        // Navigating to previous activity
        signUpBinding.backImage.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // Navigating to SignIn Activity
        signUpBinding.signInTextView.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // Handling sign up button click
        signUpBinding.signUpButton.setOnClickListener {
            handleEmailSignUp()
        }

        // Handling google button click
        signUpBinding.googleButton.setOnClickListener {
            signUpWithGoogle()
        }
    }

    // Function for handling email
    private fun handleEmailSignUp() {
        val name = signUpBinding.nameText.text.toString()
        val email = signUpBinding.emailText.text.toString()
        val password = signUpBinding.passwordText.text.toString()

        when {
            name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                showError("All fields are required!")
            }
            !isValidEmail(email) -> {
                showError("Please enter a valid email")
            }
            else -> {
                checkExistingUser(email) { exists ->
                    if (exists) {
                        showError("User is already registered")
                    } else {
                        signUpWithEmail(name, email, password)
                    }
                }
            }
        }
    }

    // Function for checking existing user
    private fun checkExistingUser(email: String, callback: (Boolean) -> Unit) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    // Check if there are ANY sign-in methods associated with this email
                    val exists = !signInMethods.isNullOrEmpty()
                    runOnUiThread {
                        callback(exists)}
                } else {
                    showError("Error checking email: ${task.exception?.message}")
                }
            }
    }

    // Function for handling Google SignUp
    private fun signUpWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {googleAccount ->
                    // First sign out from Google to prevent auto-sign in
                    googleSignInClient.signOut().addOnCompleteListener {
                        // Now check if user exists
                        checkExistingUser(googleAccount.email!!) { exists ->
                            if (exists) {
                                showError("User is already registered")
                            } else {
                                firebaseAuthWithGoogle(googleAccount.idToken!!)
                            }}
                    }
                }
            } catch (e: ApiException) {
                showError("Google Sign-In failed: ${e.message}")
            }
        }
    }

    // Function for email SignUp
    private fun signUpWithEmail(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        sendVerificationEmail(it)
                        storeUserDataInFireStore(name, email, it.uid)
                    }
                } else {
                    showError("Sign up failed: ${task.exception?.message}")
                }
            }
    }

    //Function for firebase authentication
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        sendVerificationEmail(it)
                        storeUserDataInFireStore(it.displayName!!, it.email!!, it.uid)
                    }
                } else {
                    showError("Google Sign-in failed: ${task.exception?.message}")
                }
            }
    }

    // Function for sending verification email
    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Account Created")
                        .setContentText("Check your Email to verify your account")
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            startActivity(Intent(this, SignInActivity::class.java))
                            finish()
                        }
                        .show()
                }
            }
    }

    // Function to store data in ForeStore
    private fun storeUserDataInFireStore(name: String, email: String, uid: String) {
        val user = UserDetails(name, email, uid)
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                // Data stored successfully
            }
            .addOnFailureListener { e ->
                showError("Failed to store user data: ${e.message}")
            }
    }

    // Function to show errors using sweet alert dialog
    private fun showError(message: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .setConfirmText("OK")
            .show()
    }

    //Function for checking valid email
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        return email.matches(emailRegex.toRegex())
    }
}




