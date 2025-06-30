package com.example.foodease.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodease.FoodDetailsActivity
import com.example.foodease.databinding.PopularItemListBinding
import com.example.foodease.model.MenuItem
import com.bumptech.glide.Glide
import com.example.foodease.R

// Adapter class for populating a RecyclerView with popular food items
class PopularItemAdapter(
    private val menuItems: List<MenuItem>, // List of food item names
    private val context: Context
) : RecyclerView.Adapter<PopularItemAdapter.PopularViewHolder>() {
    // RecyclerView.Adapter is responsible for managing and displaying items in a RecyclerView
    // It connects the data (food items) with the RecyclerView UI

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularViewHolder {
        // Called when a new ViewHolder instance needs to be created
        // Inflates (converts XML to UI) the layout for each item in the list
        return PopularViewHolder(PopularItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {

        val menuItem = menuItems[position]
        holder.bind(menuItem)
//        val menuItem = menuItems[position]
//        // Called when a ViewHolder needs to display data
//        val items = menuItem.foodName ?: "Food Name" // Getting the food item name at the current position
//        val images = menuItem.foodImage?: ""
//        val prices = menuItem.foodPrice ?: "Price" // Getting the corresponding price
//
//        holder.bind(items, prices, images) // Binding data to UI elements
    }

    override fun getItemCount(): Int {
        // Returns the total number of items in the list
        return menuItems.size
    }

    // ViewHolder class holds references to UI components for each list item
    class PopularViewHolder(private val binding: PopularItemListBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        private val imagesView = binding.popularImage // Reference to the ImageView inside item layout

        fun bind(menuItem: MenuItem) {
            // Setting text and image for each item in the RecyclerView
            binding.popularFoodName.text = menuItem.foodName ?: "Food Name"
            binding.popularPrice.text = menuItem.foodPrice ?: "Price"
            Glide.with(context)
                .load(menuItem.foodImage) // since images is now a URL string
                .placeholder(R.drawable.ic_launcher_background)
                .into(imagesView)

            Log.d("PopularItemAdapter", "Binding: ${menuItem.foodName}, ${menuItem.foodPrice}, ${menuItem.foodImage}")

            //setOnCLick listener to open details
            binding.root.setOnClickListener {
                val intent = Intent(context, FoodDetailsActivity::class.java).apply {
                    putExtra("MenuItemName", menuItem.foodName)
                    putExtra("MenuItemImage", menuItem.foodImage)
                    putExtra("MenuItemDescription", menuItem.foodDescription)
                    putExtra("MenuItemIngredients", menuItem.foodIngredient)
                    putExtra("MenuItemPrice", menuItem.foodPrice)

                }
                context.startActivity(intent)

            }
        }
    }
}
