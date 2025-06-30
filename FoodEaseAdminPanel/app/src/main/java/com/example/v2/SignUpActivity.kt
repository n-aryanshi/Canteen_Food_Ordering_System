package com.example.v2


import android.R
import android.app.Activity
import kotlin.text.*
import kotlin.lazy
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.ktx.Firebase
import androidx.appcompat.app.AppCompatActivity
import com.example.v2.Model.UserModel
import com.example.v2.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database


class SignUpActivity : AppCompatActivity() {

    private lateinit var nameOfRestaurant : String
    private lateinit var username : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var auth : FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient : GoogleSignInClient

    private val binding: ActivitySignUpBinding by lazy{
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.v2.R.string.default_web_client_id))  // get this from your google-services.json
            .requestEmail()
            .build()

        auth = FirebaseAuth.getInstance()

        FirebaseApp.initializeApp(this)?.let {
            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
        }

        database = Firebase.database.reference

        //initialize google sign in
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.createButton.setOnClickListener{

            //get text from editText
            username = binding.editTextName.text.toString().trim()
            nameOfRestaurant = binding.editTextRestaurantName.text.toString().trim()
            email = binding.editTextEmail.text.toString().trim()
            password = binding.editTextPassword.text.toString().trim()

            if(username.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email,password)
            }

        }

        // Add Google Sign In button click listener
        binding.googleButton.setOnClickListener {
            val signIntent =  googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.alreadyHaveBtn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
//        val locationList = arrayOf("Jaipur", "Odisha", "Bundi", "Sikar")
//        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, locationList)
//        val autoCompleteTextView = binding.listOfLocation
//        autoCompleteTextView.setAdapter(adapter)
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

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
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
        nameOfRestaurant = binding.editTextRestaurantName.text.toString().trim()
        email = binding.editTextEmail.text.toString().trim()
//        password = binding.editTextPassword.text.toString().trim()

        val user = UserModel(username, nameOfRestaurant, email)
        val userId:String = FirebaseAuth.getInstance().currentUser!!.uid
        //save user data firebase database
        database.child("admin").child(userId).setValue(user)
    }

    private fun updateUi(user: FirebaseUser?) {
        user?.let {
            val name = it.displayName ?: "No Name"
            val nameOfRestaurant = "Google User's Restaurant" // Placeholder
            val email = it.email ?: "No Email"
//            val password = "GoogleSignIn"  // or leave it empty

            val userModel = UserModel(name, nameOfRestaurant, email)
            val userId = it.uid

            Log.d("FirebaseWrite", "Saving Google user data: $userModel")

            // Save to Realtime Database
            database.child("admin").child(userId).setValue(userModel)
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