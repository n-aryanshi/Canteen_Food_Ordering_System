package com.example.v2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.v2.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient : GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.v2.R.string.default_web_client_id))  // get this from your google-services.json
            .requestEmail()
            .build()

        //initialize Firebase Auth
        auth = Firebase.auth

        //initialize firebase database
        database = FirebaseDatabase.getInstance()

        //initialize google sign in
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.loginBtn.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                checkIfUserExistsAndLogin(email, password)
                //createUserAccount()
            }
        }

        // Add Google Sign In button click listener
        binding.googleButton.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val signIntent = googleSignInClient.signInIntent
                launcher.launch(signIntent)
            }
        }

        binding.dontHaveBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkIfUserExistsAndLogin(email: String, password: String) {
        database.getReference("admin").get().addOnSuccessListener { snapshot ->
            var userExists = false
            var uid = ""

            snapshot.children.forEach { userSnapshot ->
                val userEmail = userSnapshot.child("email").value.toString()
                if (userEmail == email) {
                    userExists = true
                    uid = userSnapshot.key.toString()
                }
            }

            if (userExists) {
                // Try logging in
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "This account does not exist. Please sign up first.", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account: GoogleSignInAccount = task.result
            val email = account.email

            // Check if user with this email exists in the database
            database.getReference("admin").get().addOnSuccessListener { snapshot ->
                var userExists = false

                snapshot.children.forEach { userSnapshot ->
                    val userEmail = userSnapshot.child("email").value.toString()
                    if (userEmail == email) {
                        userExists = true
                    }
                }

                if (userExists) {
                    // Proceed with Firebase Authentication
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(this, "Successfully signed in with Google", Toast.LENGTH_SHORT).show()
                            updateUi(authTask.result?.user)
                            finish()
                        } else {
                            Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "This Google account is not registered. Please sign up first.", Toast.LENGTH_SHORT).show()
                    googleSignInClient.signOut()  // Prevent login
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Error checking registration status", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT).show()
        }
    }
}


    private fun updateUi(user: FirebaseUser?) {

        user?.let {
            val userId = it.uid
            val ref = database.getReference("admin").child(userId)

            ref.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Toast.makeText(this, "Sign-in successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "This Google account is not registered. Please sign up first.", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    googleSignInClient.signOut()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error checking registration status", Toast.LENGTH_SHORT).show()
            }
        }
    }

}