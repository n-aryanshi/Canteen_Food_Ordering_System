package com.example.foodease.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodease.R
import com.example.foodease.databinding.BuyAgainItemBinding

class BuyAgainAdapter(private val buyAgainFoodName: MutableList<String>,
                      private val buyAgainFoodPrice: MutableList<String>,
                      private val buyAgainFoodImage: MutableList<String>,
                      private var requireContext: Context
): RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

        override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
            holder.bind(buyAgainFoodName[position], buyAgainFoodPrice[position], buyAgainFoodImage[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
            val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BuyAgainViewHolder(binding)
        }

        override fun getItemCount(): Int = buyAgainFoodName.size

        inner class BuyAgainViewHolder (private val binding: BuyAgainItemBinding): RecyclerView.ViewHolder(binding.root)
        {
            fun bind(foodName: String, foodPrice: String, foodImage: String) {
                binding.buyAgainFoodName.text = foodName
                binding.buyAgainFoodPrice.text = foodPrice

                // Log the image URL for debugging
                Log.d("BuyAgainAdapter", "Image URL: $foodImage")
//
                val uri = Uri.parse(foodImage)
//                Glide.with(requireContext).load(uri).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(binding.buyAgainFoodImage)

                try {

                    Glide.with(binding.root.context)
                        .load(uri) // Use string directly without Uri.parse()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(binding.buyAgainFoodImage)
                } catch (e: Exception) {
                    Log.e("BuyAgainAdapter", "Error loading image: ${e.message}", e)
                }
            }

        }
    }