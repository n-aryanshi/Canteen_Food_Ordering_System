package com.example.foodease.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodease.R
import com.example.foodease.adapter.MenuAdapter
import com.example.foodease.databinding.FragmentMenuBottomSheetBinding
import com.example.foodease.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


//changed fragment to BottomSheetDialogFragment
//A BottomSheetDialog is a type of modal (pop-up) that slides up from the bottom of the screen.
class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    //This class is auto-generated when View Binding is enabled in the project. It corresponds to fragment_menu_bottom_sheet.xml.
    private lateinit var binding: FragmentMenuBottomSheetBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //Android me View ek UI element hota hai, jaise Button, TextView, ImageView, EditText, etc.
    //Jab hum kisi XML layout ko "inflate" karte hain, to wo ek "View Object" ban jata hai, jo screen par dikhaya ja sakta hai aur interact kiya ja sakta hai.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            dismiss()
        }
        retrieveMenuItems()

        return binding.root //actual root View return karta hai jo XML ka sabse upar wala layout hota hai (e.g., ConstraintLayout, LinearLayout).
    }



    private fun retrieveMenuItems(){
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                for(foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }

                Log.d("ITEMS", "OnDataChange: Data Received")
                //once data is received, set it to adapter
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MenuBottomSheetFragment", "Database error: ${error.message}")

            }


        })
    }

    private fun setAdapter() {
        if(menuItems.isNotEmpty()){
            val adapter = MenuAdapter(menuItems, requireContext())
            binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.menuRecyclerView.adapter = adapter
            Log.d("ITEMS", "setAdapter: data set")
        }else{
            Log.d("ITEMS", "setAdapter: data not set")
        }

    }



    companion object {

    }
}