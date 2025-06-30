package com.example.v2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.v2.Model.UserModel
import com.example.v2.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {

    private val binding : ActivityAdminProfileBinding by lazy{
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("user")

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.saveButton.setOnClickListener{
            updateUserData()

            // Disable editing after saving
            binding.editTextName.isEnabled = false
            binding.editTextAddress.isEnabled = false
            binding.editTextEmail.isEnabled = false
            binding.editTextPhone.isEnabled = false
            binding.editTextPassword.isEnabled = false
            binding.saveButton.isEnabled = false

            // Optionally change Edit button text back to "Edit"
            binding.editButton.text = "Edit"

            // Clear focus and hide cursor
            binding.editTextName.clearFocus()
            binding.editTextAddress.clearFocus()
            binding.editTextEmail.clearFocus()
            binding.editTextPhone.clearFocus()
            binding.editTextPassword.clearFocus()

        }

        binding.editTextName.isEnabled = false
        binding.editTextAddress.isEnabled = false
        binding.editTextEmail.isEnabled = false
        binding.editTextPhone.isEnabled = false
        binding.editTextPassword.isEnabled = false
        binding.saveButton.isEnabled = false


        var isEnable = false

        binding.editButton.setOnClickListener{
            isEnable = ! isEnable
            binding.editTextName.isEnabled = isEnable
            binding.editTextAddress.isEnabled = isEnable
            binding.editTextEmail.isEnabled = isEnable
            binding.editTextPhone.isEnabled = isEnable
            binding.editTextPassword.isEnabled = isEnable
            binding.saveButton.isEnabled = isEnable

            if (isEnable) {
                binding.editTextName.requestFocus()
            }
        }

        retrieveUserData()
    }


    private fun retrieveUserData() {
        val currentUserUid = auth.currentUser?.uid

        if(currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var ownerName = snapshot.child("name").getValue()
                        var email = snapshot.child("email").getValue()
                        var password = snapshot.child("password").getValue()
                        var address = snapshot.child("address").getValue()
                        var phone = snapshot.child("phone").getValue()

                        Log.d("Tag", "OnDataChange: $ownerName")

                        setDataToTextView(ownerName, email, password, address, phone)

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

     }

    private fun setDataToTextView(ownerName: Any?, email: Any?, password: Any?, address: Any?, phone: Any?) {
        binding.editTextName.setText(ownerName.toString())
        binding.editTextEmail.setText(email.toString())
        binding.editTextPassword.setText(password.toString())
        binding.editTextAddress.setText(address.toString())
        binding.editTextPhone.setText(phone.toString())
    }

    private fun updateUserData() {

        var updateName = binding.editTextName.text.toString()
        var updateEmail = binding.editTextEmail.text.toString()
        var updatePassword = binding.editTextPassword.text.toString()
        var updateAddress = binding.editTextAddress.text.toString()
        var updatePhone = binding.editTextPhone.text.toString()

        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)

            userReference.child("name").setValue(updateName)
            userReference.child("email").setValue(updateEmail)
            userReference.child("password").setValue(updatePassword)
            userReference.child("address").setValue(updateAddress)
            userReference.child("phone").setValue(updatePhone)


            Toast.makeText(this, "profile updated successfully", Toast.LENGTH_SHORT).show()

            //update the email and password for firebase authentication
            auth.currentUser?.updateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)
            } else {
                Toast.makeText(this, "profile update failed", Toast.LENGTH_SHORT).show()
            }
        }
}