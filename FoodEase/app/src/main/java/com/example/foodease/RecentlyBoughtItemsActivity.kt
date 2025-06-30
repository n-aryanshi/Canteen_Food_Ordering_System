package com.example.foodease

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodease.adapter.RecentBuyAdapter
import com.example.foodease.databinding.ActivityRecentlyBoughtItemsBinding
import com.example.foodease.model.FoodDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RecentlyBoughtItemsActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityRecentlyBoughtItemsBinding.inflate(layoutInflater)
    }

    private lateinit var allFoodNames:ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodImages: ArrayList<String>

    private lateinit var allFoodQuantities: ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        // Added null checks and better error handling
        try {
            @Suppress("UNCHECKED_CAST")
            val recentOrderItems =
                intent.getSerializableExtra("RecentBoughtOrderItem") as? ArrayList<FoodDetails>

            if (recentOrderItems.isNullOrEmpty()) {
                Log.e("RecentlyBoughtItems", "No order items received or empty list")
                // Handle empty case - maybe show a message
                return
            }

            val recentOrderItem = recentOrderItems[0]

            // Check if these lists are null before casting
            allFoodNames = recentOrderItem.foodName as? ArrayList<String> ?: ArrayList()
            allFoodPrices = recentOrderItem.foodPrices as? ArrayList<String> ?: ArrayList()
            allFoodImages = recentOrderItem.foodImages as? ArrayList<String> ?: ArrayList()
            allFoodQuantities = recentOrderItem.foodQuantities as? ArrayList<Int> ?: ArrayList()

            // Log image URLs to verify they're correct
            allFoodImages.forEachIndexed { index, url ->
                Log.d("RecentlyBoughtItems", "Image URL at index $index: $url")
            }

            setAdapter()
        } catch (e: Exception) {
            Log.e("RecentlyBoughtItems", "Error processing intent data: ${e.message}", e)
        }


//        val recentOrderItems = intent.getSerializableExtra("RecentBoughtOrderItem") as? ArrayList<FoodDetails>
//        recentOrderItems?.let { orderDetails ->
//        if(orderDetails.isNotEmpty()){
//            val recentOrderItem = orderDetails[0]
//
//            allFoodNames = recentOrderItem.foodName as ArrayList<String>
//            allFoodImages = recentOrderItem.foodImages as ArrayList<String>
//            allFoodPrices = recentOrderItem.foodPrices as ArrayList<String>
//            allFoodQuantities = recentOrderItem.foodQuantities as ArrayList<Int>
//        }
//    }
//
//        setAdapter()

    }

    private fun setAdapter() {
        val rv = binding.recentBuyRecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodPrices, allFoodImages,  allFoodQuantities)
        rv.adapter = adapter
    }
}