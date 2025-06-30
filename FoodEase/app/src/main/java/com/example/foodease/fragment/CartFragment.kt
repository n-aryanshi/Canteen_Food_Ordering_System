package com.example.foodease.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodease.CheckOutActivity
import com.example.foodease.adapter.CartAdapter
import com.example.foodease.databinding.FragmentCartBinding
import com.example.foodease.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// This class represents a Fragment that displays the cart items in a RecyclerView
class CartFragment : Fragment() {
    // View Binding: This allows us to access views in the layout file without using findViewById
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // onCreate is used for initializing anything required before the UI is created
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""


        retrieveCartItems()


        // Setting LayoutManager for RecyclerView to display items in a linear list
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

//        binding.proceedButton.setOnClickListener {
//            getOrderItemsDetail()
//        }

        return binding.root // Returning the root view of this fragment
    }

    private fun getOrderItemsDetail() {

        val orderIdReference: DatabaseReference =
//            database.reference.child("CartItems").child(userId)
            database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredients = mutableListOf<String>()

        //get Item quantities
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cartItems to respective list
                    val orderItems = foodSnapshot.getValue(CartItem::class.java)

                    //add items details in to list
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredients.add(it) }
                }
                orderNow(
                    foodName,
                    foodPrice,
                    foodDescription,
                    foodImage,
                    foodIngredients,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Order making Failed. Please Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredients: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {

        if (isAdded && context != null) {
            val intent = Intent(requireContext(), CheckOutActivity::class.java)
            intent.putExtra("foodItemName", foodName as ArrayList<String>)
            intent.putExtra("foodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("foodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("foodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("foodItemIngredients", foodIngredients as ArrayList<String>)
            intent.putExtra("foodItemQuantities", foodQuantities as ArrayList<Int>)

            startActivity(intent)
        }

    }

    private fun retrieveCartItems() {
        //db reference to the firebase
        database = FirebaseDatabase.getInstance()
       // userId = auth.currentUser?.uid ?: ""
        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        //list to store car items
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        //fetch data from the databases
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(requireContext(), "Data fetched: ${snapshot.childrenCount}", Toast.LENGTH_SHORT).show()
                for (foodSnapshot in snapshot.children) {
                    //get the cartItems object from the child node

                    val cartItems = foodSnapshot.getValue(CartItem::class.java)

                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItems?.foodImage?.let { foodImagesUri.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredients.add(it) }
                }

                setAdapter()

                binding.proceedButton.setOnClickListener {
                    if (::cartAdapter.isInitialized) {
                        getOrderItemsDetail()
                    } else {
                        Toast.makeText(requireContext(), "Cart is not loaded yet.", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            private fun setAdapter() {
                cartAdapter = CartAdapter(
                    requireContext(),
                    foodNames,
                    foodPrices,
                    foodImagesUri,
                    foodDescriptions,
                    quantity,
                    foodIngredients
                )

                binding.cartRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "data not fetched", Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun getUpdatedItemsQuantities(): MutableList<Int> {
        return quantity // or whatever your quantity list is named
    }
}
