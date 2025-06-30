package com.example.foodease

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.example.foodease.databinding.ActivityCheckOutBinding
import com.example.foodease.model.FoodDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class CheckOutActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCheckOutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var name: String
    private lateinit var enrNo: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var userId : String
    private lateinit var foodItemName : ArrayList<String>
    private lateinit var foodItemPrice : ArrayList<String>
    private lateinit var foodItemImage : ArrayList<String>
    private lateinit var foodItemDescription : ArrayList<String>
    private lateinit var foodItemIngredient : ArrayList<String>
    private lateinit var foodItemQuantities : ArrayList<Int>



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firebase and user details
        auth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance()
            .reference.child("user")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")

        //set user data
        setUserData()

        //get User details from firebase

        val intent = intent
        foodItemName = intent.getStringArrayListExtra("foodItemName") as ArrayList<String>
        foodItemPrice = intent.getStringArrayListExtra("foodItemPrice") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("foodItemImage") as ArrayList<String>
        foodItemDescription = intent.getStringArrayListExtra("foodItemDescription") as ArrayList<String>
        foodItemIngredient = intent.getStringArrayListExtra("foodItemIngredients") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("foodItemQuantities") as ArrayList<Int>

        totalAmount = "Rs. " + calculateTotalAmount().toString()
        binding.editTextAmnt.setText(totalAmount)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.placeOrderButton.setOnClickListener {

            //get data from text view
            name = binding.editTextName.text.toString().trim()
            enrNo = binding.editTextAddress.text.toString().trim()
            phone = binding.editTextPhone.text.toString().trim()


           if(name.isBlank() || enrNo.isBlank() || phone.isBlank()){
               Toast.makeText(this, "PLease enter all the details", Toast.LENGTH_SHORT).show()
           }else{
               placeOrder()
           }

        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = FirebaseDatabase.getInstance().reference.child("OrderDetails").push().key

        val foodDetails = FoodDetails(userId,name,foodItemName,foodItemPrice,foodItemImage,foodItemQuantities,enrNo,totalAmount,phone,time,itemPushKey,false,false,false)
        val orderRef = FirebaseDatabase.getInstance().reference
            .child("user").child(userId)
            .child("OrderDetails").child(itemPushKey!!)

        orderRef.setValue(foodDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(foodDetails, itemPushKey)

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
        }

    }


//    private fun addOrderToHistory(foodDetails: FoodDetails) {
//        FirebaseDatabase.getInstance().reference
//            .child("user").child(userId)
//            .child("BuyHistory").push().setValue(foodDetails)
////            .child("BuyHistory").child(userId)
////            .push()  // Use push() to create a new unique entry for each order
////            .setValue(foodDetails)
//    }

    private fun addOrderToHistory(foodDetails: FoodDetails, itemPushKey: String) {
        val historyRef = FirebaseDatabase.getInstance().reference
            .child("user").child(userId)
            .child("BuyHistory")
            .child(itemPushKey)

        historyRef.setValue(foodDetails).addOnFailureListener {
            Toast.makeText(this, "Failed to save order to history", Toast.LENGTH_SHORT).show()
        }
    }


    private fun removeItemFromCart() {

       val cartItemReference = FirebaseDatabase.getInstance().reference
           .child("user").child(userId)
           .child("CartItems")
//           .child(userId)

        cartItemReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Cart cleared!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to clear cart", Toast.LENGTH_SHORT).show()
            }


    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodItemPrice.size) {
            val price = foodItemPrice[i]
//            val lastChar = price.last()
            val priceIntValue = price.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0
            val quantity = foodItemQuantities[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
       val user = auth.currentUser
       if(user!= null){
//           val userId = user.uid
//           val userReference = databaseReference.child("user").child(userId)
           val userReference = databaseReference

           userReference.addListenerForSingleValueEvent(object: ValueEventListener{
               override fun onDataChange(snapshot: DataSnapshot) {
                   if(snapshot.exists()){
                       val name = snapshot.child("name").getValue(String::class.java)?:""
                       val address = snapshot.child("enrollment").getValue(String::class.java)?:""
                       val phone = snapshot.child("phone").getValue(String::class.java)?:""

                        binding.apply{
                            editTextName.setText(name)
                            editTextAddress.setText(address)
                            editTextPhone.setText(phone)
                        }
                   }
               }

               override fun onCancelled(error: DatabaseError) {
                   Toast.makeText(this@CheckOutActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
               }

           })
       }
    }
}


