package com.example.foodease.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodease.R
import com.example.foodease.adapter.PopularItemAdapter
import com.example.foodease.databinding.FragmentHomeBinding
import com.example.foodease.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class HomeFragment : Fragment() {
    // View binding is used to interact with UI elements without using findViewById
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewMenuButton.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        retrieveAndDisplayPopularItem()
        return binding.root // The root view of the fragment (final UI that will be displayed)
    }

    private fun retrieveAndDisplayPopularItem() {
        //get reference to the database
        database = FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        //retrieve menuItems from the database
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)

                    if (menuItem == null) {
                        Log.e("FirebaseParse", "Failed to parse item: ${foodSnapshot.key}")
                    } else {
                        menuItems.add(menuItem)
                    }

//                    menuItem?.let {
//                        menuItems.add(it)
//                    }
                }
                    //display a random popular items
                    randomPopularItems()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load menu items", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Database error: ${error.message}")

            }

        })


    }

    private fun randomPopularItems() {
        //create as shuffled list of menu items
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subsetMenuItems = index.take(numItemToShow).map{ menuItems[it]}

        setPopularItemsAdapter(subsetMenuItems)

    }

    private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItem>) {
        val adapter = PopularItemAdapter(subsetMenuItems, requireContext())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter= adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This method is called after onCreateView(), and it's used to set up UI components

        // Setting up Image Slider (a slideshow of images in the app UI)
        val imageList = ArrayList<SlideModel>() // Creating a list to store images
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT)) // Adding images to the list
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))


        val imageSlider = binding.imageSlider // Accessing ImageSlider from XML using ViewBinding
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT) // Ensuring images scale properly

        // Handling click events on the image slider
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                //
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                // position: Index of the selected image in the slider
                val itemMessage = "Selected Image $position" // Creating a message
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
                // Showing a short pop-up message (Toast) when an image is selected
            }
        })
    }


    companion object {

    }
}