package com.example.foodease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.foodease.databinding.ActivitySignupBinding
import com.example.foodease.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.foodease.R.string.default_web_client_id))  // get this from your google-services.json
            .requestEmail()
            .build()

        //initialize firebase auth
        auth = Firebase.auth

        //initialize firebase database
        database = Firebase.database.reference

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.createBtn.setOnClickListener {
            username = binding.editTextName.text.toString()
            email = binding.editTextEmail.text.toString()
            password = binding.editTextPassword.text.toString()

            if (email.isEmpty() || password.isBlank() || username.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        binding.googleBtn.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.alreadyHaveBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                    saveUserData()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                    Log.d("Account", "createAccount: Failure", task.exception)
                }
            }
    }

private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account: GoogleSignInAccount = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    //successfully sign in with google
                    Toast.makeText(
                        this,
                        "Successfully sign-in with Google",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    updateUi(authTask.result?.user)
                    finish()
                } else {
                    Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    } else {
        Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT).show()
    }
}

    private fun saveUserData() {
        username = binding.editTextName.text.toString().trim()
        email = binding.editTextEmail.text.toString().trim()
        //password = binding.editTextPassword.text.toString().trim()

        val user = UserModel(username, email)
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
        //save user data firebase database
        database.child("user").child(userId).setValue(user)
    }

    private fun updateUi(user: FirebaseUser?) {

        user?.let {
            val name = it.displayName ?: "No Name"
            val email = it.email ?: "No Email"
//            val password = "GoogleSignIn"  // or leave it empty

            val userModel = UserModel(name, email)
            val userId = it.uid

            // Save to Realtime Database
            database.child("user").child(userId).setValue(userModel)
                .addOnSuccessListener {
                    Toast.makeText(this, "Google user data saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                    Log.e("GoogleSave", "Error saving Google user", error)
                }
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

