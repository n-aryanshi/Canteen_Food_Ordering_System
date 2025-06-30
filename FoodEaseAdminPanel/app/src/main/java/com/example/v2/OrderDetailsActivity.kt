@file:Suppress("DEPRECATION")

package com.example.v2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.v2.Model.FoodDetails
import com.example.v2.adapter.OrderDetailsAdapter
import com.example.v2.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy{
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private var userName: String?= null
    private var address: String?= null
    private var phoneNumber: String?= null
    private var totalPrice: String? = null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantities: ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d("OrderDetailsActivity", "onCreate called")

        binding.backBtn.setOnClickListener {
            finish()
        }
        getDataFromIntent()
    }

//    private fun getDataFromIntent() {
//        val receivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as FoodDetails
//        receivedOrderDetails?.let{
//            userName = it.userName
//            foodNames = ArrayList(it.foodNames)
//            foodImages = ArrayList(it.foodImages)
//            foodQuantity = ArrayList(it.foodQuantities)
//            address = it.address
//            phoneNumber = it.phoneNumber
//            foodPrices = ArrayList(it.foodPrices)
//            totalPrice = it.totalPrice
//
//            setUserDetails()
//            setAdapter()
//
//        }
//    }

    private fun getDataFromIntent() {
        Log.d("OrderDetailsActivity", "getDataFromIntent called")
        val receivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as? FoodDetails
        if(receivedOrderDetails == null) {
            Log.e("OrderDetailsActivity", "No data received in intent!")
            finish()
            return
        }

        receivedOrderDetails?.let {
            userName = it.userName
            address = it.enrollment
            phoneNumber = it.phoneNumber
            totalPrice = it.totalPrice

            // Handle possible nulls safely
            foodNames = ArrayList(it.foodName ?: emptyList())
            foodImages = ArrayList(it.foodImages ?: emptyList())
            foodQuantities = ArrayList(it.foodQuantities ?: emptyList())
            foodPrices = ArrayList(it.foodPrices ?: emptyList())

            setUserDetails()
            Log.d("AdapterCheck", "Names: $foodNames, Images: $foodImages, Qty: $foodQuantities, Prices: $foodPrices")
            setAdapter()
        } ?: run {
            // Optional: log or show an error if data is missing
            Log.e("OrderDetailsActivity", "Intent extras were null or invalid")
            finish()  // gracefully close the activity
        }
    }


    private fun setAdapter() {
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.visibility = View.VISIBLE
        val adapter = OrderDetailsAdapter(this, foodNames, foodQuantities, foodPrices, foodImages)
        binding.rv.adapter = adapter
    }

    private fun setUserDetails() {
        binding.editTextName.setText(userName)
        binding.editTextAddress.setText(address)
        binding.editTextPhone.setText(phoneNumber)
        binding.editTextAmnt.text = "$totalPrice"
    }
}