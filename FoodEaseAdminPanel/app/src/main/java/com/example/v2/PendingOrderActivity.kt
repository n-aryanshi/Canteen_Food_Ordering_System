package com.example.v2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.v2.Model.FoodDetails
import com.example.v2.adapter.PendingOrderAdapter
import com.example.v2.databinding.ActivityPendingOrderBinding
import com.google.firebase.database.*

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {

    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> =  mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: MutableList<FoodDetails> = mutableListOf()

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialisation of database
        database = FirebaseDatabase.getInstance()

        //initialisation of database reference
        databaseOrderDetails = database.reference.child("OrderDetails")

        getOrderDetails()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }



    private fun getOrderDetails() {
        val usersRef = FirebaseDatabase.getInstance().getReference("user")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    val orderDetailsNode = userSnapshot.child("OrderDetails")

                    for (orderSnapshot in orderDetailsNode.children) {
                        val orderDetails = orderSnapshot.getValue(FoodDetails::class.java)
                        if (orderDetails != null && orderDetails.orderDispatched != true) {
                            orderDetails.userUid = userId  // important for later update
                            listOfOrderItem.add(orderDetails)
                        }
                    }
                }

                addDataToListRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PendingOrderActivity, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun addDataToListRecyclerView() {
        for (orderItem in listOfOrderItem) {
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
//            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach {
//                listOfImageFirstFoodOrder.add(it)
//            }

            // ✅ Only add the first non-empty image
            val firstImage = orderItem.foodImages?.firstOrNull { it.isNotEmpty() }
            listOfImageFirstFoodOrder.add(firstImage ?: "")
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        //val acceptedStatusList = listOfOrderItem.map { it.orderAccepted == true }.toMutableList()

        val adapter = PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder,this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("UserOrderDetails", listOfOrderItem[position])
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        val itemKey = listOfOrderItem[position].itemPushKey
        val userId = listOfOrderItem[position].userUid

        itemKey?.let {
            // Update order status in OrderDetails
//            val ref = database.reference.child("OrderDetails").child(it)
//            ref.child("orderAccepted").setValue(true)

            // Also update user's BuyHistory
            if (userId != null && itemKey != null) {

                val userOrderDetailsRef = database.reference.child("user").child(userId).child("OrderDetails").child(itemKey)
                userOrderDetailsRef.child("orderAccepted").setValue(true)

                val userOrderHistoryRef = database.reference.child("user").child(userId).child("BuyHistory").child(itemKey)
                userOrderHistoryRef.child("orderAccepted").setValue(true)

                // ✅ Send Notification to User
                sendUserNotification(userId, "Your order has been accepted", "success")
            }
        }
        updateOrderAcceptStatus(position)


    }



    override fun onItemDispatchClickListener(position: Int) {
        val dispatchKey = listOfOrderItem[position].itemPushKey
        val userId = listOfOrderItem[position].userUid

        if (!dispatchKey.isNullOrEmpty() && !userId.isNullOrEmpty()) {

            val completedOrderRef = database.reference.child("CompletedOrder").child(dispatchKey)
            val orderData = listOfOrderItem[position]
            orderData.orderDispatched = true
            // Save to CompletedOrder node
            completedOrderRef.setValue(orderData).addOnSuccessListener {
                deleteThisItemFromOrderDetail(userId, dispatchKey)

                val userBuyHistoryRef = database.reference.child("user").child(userId).child("BuyHistory").child(dispatchKey)
                userBuyHistoryRef.child("orderDispatched").setValue(true)

                // ✅ Optional: update OrderDetails too before removing it
                val userOrderRef = database.reference.child("user").child(userId).child("OrderDetails").child(dispatchKey)
                userOrderRef.child("orderDispatched").setValue(true)

                sendUserNotification(userId, "Your order has been dispatched", "info")


//                userOrderRef.removeValue().addOnSuccessListener {
//
//                    // ✅ Send Notification
//                    sendUserNotification(userId, "Your order has been dispatched", "info")
//
//                    // ✅ Remove from local list
//                    listOfOrderItem.removeAt(position)
//                    listOfName.removeAt(position)
//                    listOfTotalPrice.removeAt(position)
//                    listOfImageFirstFoodOrder.removeAt(position)
//                    binding.pendingOrderRecyclerView.adapter?.notifyItemRemoved(position)
//
//                    Toast.makeText(this, "Order dispatched successfully", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "Failed to dispatch order", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun updateOrderAcceptStatus(position: Int) {
        val userId = listOfOrderItem[position].userUid
        val itemKey = listOfOrderItem[position].itemPushKey
//        if (userId != null && itemKey != null) {
//            val ref = database.reference.child("user").child(userId).child("BuyHistory").child(itemKey)
//            ref.child("orderAccepted").setValue(true)
//            databaseOrderDetails.child(itemKey).child("orderAccepted").setValue(true)
//        }

        if (userId != null && itemKey != null) {
            databaseOrderDetails.child(itemKey).child("orderAccepted").setValue(true)
            val userOrderDetailsRef = database.reference.child("user").child(userId).child("OrderDetails").child(itemKey)
            userOrderDetailsRef.child("orderAccepted").setValue(true)

            val userOrderHistoryRef = database.reference.child("user").child(userId).child("BuyHistory").child(itemKey)
            userOrderHistoryRef.child("orderAccepted").setValue(true)
//            databaseOrderDetails.child(itemKey).child("orderAccepted").setValue(true)
        }
    }

    private fun deleteThisItemFromOrderDetail(userId: String, itemKey: String) {
        val ref = database.reference.child("user").child(userId).child("OrderDetails").child(itemKey)
//        val ref = database.reference.child("OrderDetails").child(itemKey)
        ref.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Order is Dispatched", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Order dispatching failed", Toast.LENGTH_SHORT).show()
        }
//        databaseOrderDetails.child(itemKey).child("orderAccepted").setValue(true)
    }

    override fun getOrderItem(position: Int): FoodDetails {
        return listOfOrderItem[position]
    }

    private fun sendUserNotification(userId: String, message: String, type: String) {
        val notificationRef = database.reference.child("user").child(userId).child("Notifications").push()
        val notificationData = mapOf(
            "message" to message,
            "type" to type,
            "timestamp" to System.currentTimeMillis()
        )

        notificationRef.setValue(notificationData)
    }

}

