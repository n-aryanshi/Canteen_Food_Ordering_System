package com.example.foodease.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodease.FoodDetailsActivity
import com.example.foodease.databinding.MenuItemBinding
import com.example.foodease.model.MenuItem

//MenuAdapter is inheriting from RecyclerView.Adapter<MenuAdapter.MenuViewHolder>().
class MenuAdapter(private val menuItems: List<MenuItem>,
                  private val requireContext: Context
): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    //RecyclerView.Adapter<T> is a generic class that helps manage and display a list of items in a RecyclerView.
    //It is responsible for creating, binding, and tracking the number of items.


    /**
     * onCreateViewHolder is called when a new ViewHolder is created.
     * This method inflates (converts XML to View) the layout for individual menu items.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        // Using ViewBinding to inflate the layout for each menu item
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    /**
     * onBindViewHolder binds data to each ViewHolder.
     * This method updates the UI elements for each item at a given position.
     */

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    //getItemCount returns the total number of menu items.
    override fun getItemCount(): Int = menuItems.size

    //MenuViewHolder is responsible for holding and managing views of individual items in RecyclerView.
    //Each MenuViewHolder represents one row/item in the RecyclerView.
    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            init{
                binding.root.setOnClickListener{
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        openDetailsActivity(position)
                    }

//                    //set onclick listener to open details
//                    val intent = Intent(requireContext, FoodDetailsActivity::class.java)
//                    intent.putExtra("MenuItemName", detailsFoodName.get(position))
//                    intent.putExtra("MenuItemImage", detailsFoodImage.get(position))
//                    requireContext.startActivity(intent)
                }
            }

        private fun openDetailsActivity(position: Int){
            val menuItem = menuItems[position]

            val intent = Intent(requireContext, FoodDetailsActivity::class.java).apply{
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredients", menuItem.foodIngredient)
                putExtra("MenuItemPrice", menuItem.foodPrice)
            }

            //start the details activity
            requireContext.startActivity((intent))
        }



        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuFoodPrice.text = menuItem.foodPrice
                var uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuFoodImage)



                }
            }
        }
}


/**
 * RecyclerView	The restaurant itself (showing many menu items). -> A list/grid of UI elements (e.g., news feed, product list).
 * Adapter	The chef (prepares the dishes). -> Prepares and organizes data for RecyclerView.
 * Inflater	The kitchen staff placing food on a tray. -> Converts XML layout into a real view that the user sees.
 * Binding	The tray (makes it easier for the waiter). -> Makes accessing views easier inside ViewHolder.
 * ViewHolder	The waiter (carries and serves food). -> Holds the views inside a single item of the list.
 */