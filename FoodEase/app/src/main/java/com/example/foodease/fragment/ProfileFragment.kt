package com.example.foodease.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodease.R
import com.example.foodease.databinding.FragmentProfileBinding
import com.example.foodease.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container,false)

        setUserData()
        binding.apply{
            editTextName.isEnabled = false
            editTextAddress.isEnabled = false
            editTextEmail.isEnabled = false
            editTextPhone.isEnabled = false

            binding.editBtn.setOnClickListener {
                editTextName.isEnabled = !editTextName.isEnabled
                editTextAddress.isEnabled = !editTextAddress.isEnabled
                editTextEmail.isEnabled = !editTextEmail.isEnabled
                editTextPhone.isEnabled = !editTextPhone.isEnabled
            }


        }

        binding.saveInfoBtn.setOnClickListener{
            val name = binding.editTextName.text.toString()
            val address = binding.editTextAddress.text.toString()
            val email = binding.editTextEmail.text.toString()
            val phone = binding.editTextPhone.text.toString()

            updateUserData(name,email, address, phone)
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid
        if(userId != null){
            val userReference = database.getReference("user").child(userId)
            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone,
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(), "profile updated successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "profile updation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if(userId!= null){
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if(userProfile != null){
                            binding.editTextName.setText(userProfile.name)
                            binding.editTextAddress.setText(userProfile.enrollment)
                            binding.editTextEmail.setText(userProfile.email)
                            binding.editTextPhone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    companion object {
        //val userId = auth
    }
}