package com.example.foodease

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.foodease.databinding.ActivityFoodDetailsBinding
import com.example.foodease.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FoodDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailsBinding
    private lateinit var auth: FirebaseAuth

    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredients: String? = null
    private var foodPrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodImage = intent.getStringExtra("MenuItemImage")

        with(binding) {
            detailsFoodName.text = foodName
            descriptionText.text = foodDescription
            ingredientsText.text = foodIngredients

//            Glide.with(this@FoodDetailsActivity).load(Uri.parse(foodImage)).into(detailsFoodImage)

            if (!foodImage.isNullOrEmpty()) {
                Glide.with(this@FoodDetailsActivity).load(Uri.parse(foodImage)).into(binding.detailsFoodImage)
            } else {
                binding.detailsFoodImage.setImageResource(R.drawable.ic_launcher_background) // use your fallback image
            }
        }



        binding.imageButton.setOnClickListener {
            finish()
        }

        binding.addToCartButton.setOnClickListener {
            addItemToCart()
        }

    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""

        val cartItemRef = database.child("user").child(userId).child("CartItems")

        cartItemRef.get().addOnSuccessListener { snapshot ->
            var itemExists = false

            for (itemSnapshot in snapshot.children) {
                val existingName = itemSnapshot.child("foodName").getValue(String::class.java)
                if (existingName == foodName) {
                    // Item already exists - update its quantity
                    val currentQuantity = itemSnapshot.child("foodQuantity").getValue(Int::class.java) ?: 1
                    val newQuantity = currentQuantity + 1

                    itemSnapshot.ref.child("foodQuantity").setValue(newQuantity)
                    Toast.makeText(this, "Increased quantity in cart", Toast.LENGTH_SHORT).show()
                    itemExists = true
                    break
                }
            }

            if (!itemExists) {
                // Item does not exist - add as new
                val cartItem = CartItem(
                    foodName.toString(),
                    foodPrice.toString(),
                    foodImage.toString(),
                    1,
                    foodDescription.toString(),
                    foodIngredients.toString()
                )

                cartItemRef.push().setValue(cartItem).addOnSuccessListener {
                    Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Error accessing cart", Toast.LENGTH_SHORT).show()
        }

    }
}