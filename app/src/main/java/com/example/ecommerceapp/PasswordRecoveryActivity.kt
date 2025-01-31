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
    private val passwordRecoveryBinding: ActivityPasswordRecoveryBinding by lazy{
        ActivityPasswordRecoveryBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(passwordRecoveryBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()

        //Switching to previous activity
        passwordRecoveryBinding.backImage.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        passwordRecoveryBinding.continueButton.setOnClickListener {
            val email = passwordRecoveryBinding.emailText.text.toString()
            if (email.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Please Enter Your Email")
                    .setConfirmText("OK")
                    .show()
            }
            else{
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
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