package com.example.v2.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.v2.Model.AllMenu
import com.example.v2.databinding.MenuItemBinding
import com.google.firebase.database.DatabaseReference

class ViewAllMenuAdapter(
    private val context: Context,
    private val menuList:ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position: Int) -> Unit


) : RecyclerView.Adapter<ViewAllMenuAdapter.AddMenuViewHolder>() {

    private val itemQuantities = IntArray(menuList.size){1}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddMenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddMenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddMenuViewHolder(private val binding: MenuItemBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(position:Int){
            binding.apply{
                val quantity = itemQuantities[position]
                val menuItem = menuList[position]
                val uriString = menuItem.foodImage
                val uri =  Uri.parse(uriString.toString())

                menuFoodName.text = menuItem.foodName
                menuFoodPrice.text = menuItem.foodPrice
                Glide.with(context).load(uri).into(menuFoodImage)
                itemQuantity.text = quantity.toString()

                plusButton.setOnClickListener {
                    increaseQuantity(position)
                }

                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }
               deleteButton.setOnClickListener {
                   onDeleteClickListener(position)
                }

            }
        }

        private fun increaseQuantity(position: Int) {
            if(itemQuantities[position] < 10){
                itemQuantities[position]++
                binding.itemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun decreaseQuantity(position: Int) {
            if(itemQuantities[position] > 1){
                itemQuantities[position]--
                binding.itemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteQuantity(position: Int) {
            menuList.removeAt(position)
            menuList.removeAt(position)
            menuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,menuList.size )
        }

    }
}