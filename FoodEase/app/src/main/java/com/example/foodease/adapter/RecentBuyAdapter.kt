package com.example.foodease.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodease.R
import com.example.foodease.databinding.RecentBoughtItemBinding

class RecentBuyAdapter(
    private var context: Context,
    private var foodNameList: ArrayList<String>,
    private var foodPriceList: ArrayList<String>,
    private var foodImageList: ArrayList<String>,
    private var foodQuantityList: ArrayList<Int>

    ) : RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
       val binding = RecentBoughtItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int = foodNameList.size

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)

    }

    inner class RecentViewHolder(private val binding: RecentBoughtItemBinding
    ):RecyclerView.ViewHolder(binding.root) {

        fun bind(position:Int){
            binding.apply{
                recentFoodName.text = foodNameList[position]
                recentFoodPrice.text = foodPriceList[position]
                actualQuantity.text = foodQuantityList[position].toString()


                val uriString = foodImageList[position]
//
                Log.d("RecentBuyAdapter", "Image URL at position $position: $uriString")
                val uri = Uri.parse(uriString)
//                Glide.with(context).load(uri).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(recentFoodImage)

                try {
                    Glide.with(context)
                        .load(uri) // Use string directly without Uri.parse()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(recentFoodImage)
                } catch (e: Exception) {
                    Log.e("RecentBuyAdapter", "Error loading image: ${e.message}", e)
                }
            }
        }

    }
 }