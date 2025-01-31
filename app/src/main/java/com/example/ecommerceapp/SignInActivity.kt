package com.example.ecommerceapp
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.ecommerceapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignInActivity : AppCompatActivity() {
    //Applying binding
    private val signInBinding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    //Initializing firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    //Initializing Google Sign In
    private lateinit var googleSignInClient: GoogleSignInClient
    //Initializing request code
    private val RC_SIGN_IN = 1001
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(signInBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Initializing firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Google Sign-In setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        //Creating a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //Handling SignIn button click
        signInBinding.signInButton.setOnClickListener{
            //Getting texts from text fields
            val email = signInBinding.emailText.text.toString()
            val password = signInBinding.passwordText.text.toString()
            //Checking if fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                //Generating an error message
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("All fields are required!")
                    .setConfirmText("OK")
                    .show()
            }
            else{
                //Email Sign In function call
                signInWithEmail(email, password)
            }

        }
        //Google Sign In functionality
        signInBinding.googleSignInButton.setOnClickListener {
            //Google sign in Function call
            signInWithGoogle()
        }
        //Switching to sign up activity
        signInBinding.signUpTextView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        //Switching to password recovery activity
        signInBinding.recoveryPassword.setOnClickListener {
            startActivity(Intent(this, PasswordRecoveryActivity::class.java))

        }
    }
    // Login with Email/Password
    private fun signInWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        navigateToNextActivity()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Please verify Your Email")
                            .setConfirmText("OK")
                            .show()
                    }
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Wrong Email or Password")
                        .setConfirmText("OK")
                        .show()
                }
            }
    }

    // Google Sign-in
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Handle Google Sign-in result
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    firebaseAuthWithGoogle(it.idToken!!)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firebase Auth with Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        navigateToNextActivity()
                    } else {
                        Toast.makeText(this, "Please verify your email.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Google Sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Navigate to the next activity
    private fun navigateToNextActivity() {
        val intent = Intent(this, StoreActivity::class.java)
        startActivity(intent)
        finish()
    }
}


