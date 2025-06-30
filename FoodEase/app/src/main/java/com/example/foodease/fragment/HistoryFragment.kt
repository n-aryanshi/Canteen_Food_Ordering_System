package com.example.foodease.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodease.R
import com.example.foodease.RecentlyBoughtItemsActivity
import com.example.foodease.adapter.BuyAgainAdapter
import com.example.foodease.databinding.FragmentHistoryBinding
import com.example.foodease.model.FoodDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItems: MutableList<FoodDetails> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retrieveBuyHistory()

        binding.recentBought.setOnClickListener {
            seeItemsRecentlyBought()
        }
//
        binding.receiveStatus.setOnClickListener {
            updateOrderStatus()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItems[0].itemPushKey
        val completeOrderReference = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderReference.child("paymentReceived").setValue(true)
    }

    //seeing recently bought items
    private fun seeItemsRecentlyBought() {
        listOfOrderItems.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), RecentlyBoughtItemsActivity::class.java)
            intent.putExtra("RecentBoughtOrderItem", listOfOrderItems as Serializable)
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        binding.recentBought.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentItem")

        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(FoodDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItems.add(it)
                    }
                }
                listOfOrderItems.reverse()
                if (listOfOrderItems.isNotEmpty()) {
                    setDataInRecentBoughtItem()
                    setPreviouslyBoughtItemRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HistoryFragment", "Failed to retrieve data: ${error.message}")
            }

        })
    }

    private fun setDataInRecentBoughtItem() {
        binding.recentBought.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItems.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainFoodName.text = it.foodName?.firstOrNull() ?: ""
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull() ?: ""

                val image = it.foodImages?.firstOrNull() ?: ""
//
//                // Log the image URL for debugging
                Log.d("RecentBoughtItem", "Image URL: $image")
//
//                val uri = Uri.parse(image)
//                Glide.with(requireContext()).load(uri).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(buyAgainFoodImage)
                try {
                    Glide.with(binding.root.context)
                        .load(image) // Use string directly without Uri.parse()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(buyAgainFoodImage)
                } catch (e: Exception) {
                    Log.e("HistoryFragment", "Error loading image: ${e.message}", e)
                }



//                val isOrderAccepted = listOfOrderItems[0].orderAccepted == true
//                Log.d("Tag", "setDataInRecentBoughtItem: $isOrderAccepted ")
//                if (isOrderAccepted) {
//                    cv1.background.setTint(Color.GREEN)
//                    receiveStatus.visibility = View.VISIBLE
//                }

                // Handle the status colors and text
                val isAccepted = it.orderAccepted ?: false
                val isDispatched = it.orderDispatched ?: false
                val isPaid = it.paymentReceived ?: false

                when {
                    isPaid -> {
                        cv1.background.setTint(Color.GREEN)
                        binding.cv1.invalidate()
                        receiveStatus.text = "Received"
                        receiveStatus.visibility = View.VISIBLE
                    }
                    isDispatched -> {
                        cv1.background.setTint(Color.YELLOW)
                        binding.cv1.invalidate()
                        receiveStatus.text = "Dispatched"
                        receiveStatus.visibility = View.VISIBLE
                    }
                    isAccepted -> {
                        cv1.background.setTint(Color.parseColor("#FFA500")) // Orange
                        binding.cv1.invalidate()
                        receiveStatus.text = "Accepted"
                        receiveStatus.visibility = View.VISIBLE
                    }
                    else -> {
                        cv1.background.setTint(Color.RED)
                        binding.cv1.invalidate()
                        receiveStatus.text = "Waiting"
                        receiveStatus.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    private fun setPreviouslyBoughtItemRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for (i in 1 until listOfOrderItems.size) {
            listOfOrderItems[i].foodName?.firstOrNull()?.let {
                buyAgainFoodName.add(it)
            }
            listOfOrderItems[i].foodPrices?.firstOrNull()?.let {
                buyAgainFoodPrice.add(it)
            }
            listOfOrderItems[i].foodImages?.firstOrNull()?.let {
                buyAgainFoodImage.add(it)
            }
        }


        val rv = binding.buyAgainRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter = BuyAgainAdapter(
            buyAgainFoodName,
            buyAgainFoodPrice,
            buyAgainFoodImage,
            requireContext()
        )
        rv.adapter = buyAgainAdapter


    }
}


