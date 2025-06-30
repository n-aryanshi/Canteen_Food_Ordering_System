package com.example.v2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.v2.Model.FoodDetails
import com.example.v2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {


    private val binding : ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completedOrderReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()


        binding.addMenu.setOnClickListener{
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }

        binding.viewItemMenu.setOnClickListener{
            val intent = Intent(this, ViewAllMenuActivity::class.java)
            startActivity(intent)
        }

        binding.orderDispatched.setOnClickListener{
            val intent = Intent(this, DeliveryInfoActivity::class.java)
            startActivity(intent)
        }

        binding.adminProfile.setOnClickListener{
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }

        binding.pendingOrderTextView.setOnClickListener{
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        pendingOrder()
        completedOrder()
        wholeTimeEarning()

    }

    private fun wholeTimeEarning() {
        val reference = database.reference.child("CompletedOrder")
//        val listOfTotalPay = mutableListOf<Int>()

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalEarnings = 0
                for(orderSnapshot in snapshot.children){
                    var completeOrder = orderSnapshot.getValue(FoodDetails::class.java)

                    val rawPrice = completeOrder?.totalPrice
                    rawPrice?.let {
                        // Remove "Rs.", "$", or any symbols
                        val cleanPrice = it.replace(Regex("[^0-9]"), "")
                        val price = cleanPrice.toIntOrNull()
                        if (price != null) {
                            totalEarnings += price
                        }
                    }

//                    val price = completeOrder?.totalPrice?.replace("$","")?.toIntOrNull()
////                        ?.let{ i ->
////                            listOfTotalPay.add(i)
////                        }
//                    if (price != null) listOfTotalPay.add(price)
                }
                //binding.noOfTotalEarnings.text = listOfTotalPay.sum().toString() + "$"
                binding.noOfTotalEarnings.text = "Rs. $totalEarnings"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private fun completedOrder() {
        val completedOrderReference = database.reference.child("CompletedOrder")
        var completedOrderItemCount = 0
        completedOrderReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrderItemCount = snapshot.childrenCount.toInt()
                binding.noOfCompleted.text = completedOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

//    private fun pendingOrder() {
//
//        database = FirebaseDatabase.getInstance()
//        var pendingOrderReference = database.reference.child("OrderDetails")
//        var pendingOrderItemCount = 0
//        pendingOrderReference.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                pendingOrderItemCount = snapshot.childrenCount.toInt()
//                binding.noOfPendings.text = pendingOrderItemCount.toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//
//    }

    private fun pendingOrder() {
        val userReference = database.reference.child("user")
        var pendingOrderItemCount = 0

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount = 0

                for (userSnapshot in snapshot.children) {
                    val orderDetails = userSnapshot.child("OrderDetails")
                    pendingOrderItemCount += orderDetails.childrenCount.toInt()
                }

                binding.noOfPendings.text = pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }



}