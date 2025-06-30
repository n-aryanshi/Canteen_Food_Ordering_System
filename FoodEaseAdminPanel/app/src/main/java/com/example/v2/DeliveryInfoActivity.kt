package com.example.v2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.v2.Model.FoodDetails
import com.example.v2.adapter.DeliveryInfoAdapter
import com.example.v2.databinding.ActivityDeliveryInfoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeliveryInfoActivity : AppCompatActivity() {
    private val binding : ActivityDeliveryInfoBinding by lazy {
        ActivityDeliveryInfoBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<FoodDetails> = arrayListOf()
    lateinit var adapter: DeliveryInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root) //out for delivery

        binding.backBtn.setOnClickListener {
            finish()
        }

        retrieveCompleteOrderDetail()

       // val adapter = DeliveryInfoAdapter()
//        binding.deliveryRecyclerView.adapter = adapter
//        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
    }



    private fun retrieveCompleteOrderDetail(){

        //Initialize firebase database
        database = FirebaseDatabase.getInstance()
        val completeOrderReference = database.reference.child("CompletedOrder").orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear the list before populating it with new data
                listOfCompleteOrderList.clear()

                for(orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(FoodDetails::class.java)
                    completeOrder?.let{
                        listOfCompleteOrderList.add(it)
                    }
                }
                //reverse the list to display latest order first
                listOfCompleteOrderList.reverse()

                setDataIntoRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setDataIntoRecyclerView() {

        //initialization list to hold customers name and payment status
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()
        val totalPrice = mutableListOf<String>()

        for (order in listOfCompleteOrderList) {
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
            totalPrice.add(order.totalPrice ?: "0")
        }

        adapter = DeliveryInfoAdapter(customerName, moneyStatus, totalPrice) { position ->
            // Update the paymentReceived locally
            moneyStatus[position] = true
            listOfCompleteOrderList[position].paymentReceived = true

            // Update Firebase
            val order = listOfCompleteOrderList[position]
            val database = FirebaseDatabase.getInstance()


            val userId = order.userUid ?: return@DeliveryInfoAdapter
            val itemKey = order.itemPushKey ?: return@DeliveryInfoAdapter

            val completedOrderRef = database.reference.child("CompletedOrder").child(itemKey).child("paymentReceived").setValue(true)

            // ✅ Remove from global OrderDetails node
//            val orderDetailsRef = database.reference.child("OrderDetails").child(itemKey)
//            orderDetailsRef.removeValue()

            // 1. ✅ Mark paymentReceived as true
            order.paymentReceived = true

            // 2. ✅ Store order in BuyHistory under user's node
            val buyHistoryRef = database.reference
                .child("user").child(userId)
                .child("BuyHistory").child(itemKey)

            buyHistoryRef.setValue(order).addOnSuccessListener {
                // 3. ✅ Remove from OrderDetails after successful payment & move
                //database.reference.child("OrderDetails").child(itemKey).removeValue()

                database.reference.child("user").child(userId)
                    .child("OrderDetails").child(itemKey).removeValue()

//                // 4. ✅ Optionally update the CompletedOrder node too
//                database.reference.child("CompletedOrder").child(itemKey)
//                    .child("paymentReceived").setValue(true)
            }

            // Notify adapter about change
            adapter.notifyItemChanged(position)
        }

        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)


    }
}