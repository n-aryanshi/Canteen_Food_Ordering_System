package com.example.v2.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.v2.R
import com.example.v2.databinding.OrderDetailItemBinding

class OrderDetailsAdapter(
    private var context: Context,
    private var foodNames: ArrayList<String>,
    private var foodQuantity: ArrayList<Int>,
    private var foodPrice: ArrayList<String>,
    private var foodImage: ArrayList<String>,
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding =
            OrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailsViewHolder(binding)

    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    inner class OrderDetailsViewHolder( private val binding: OrderDetailItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.apply{
//                orderFoodName.text= foodName[position]
//                totalAmount.text = foodQuantity[position].toString()

                orderFoodName.text = foodNames.getOrNull(position) ?: "N/A"
                totalAmount.text = foodQuantity.getOrNull(position)?.toString() ?: "0"
                orderFoodPrice.text = foodPrice.getOrNull(position) ?: "₹0"

                val uriString = foodImage[position]
//                val uri = Uri.parse(uriString)
//                Glide.with(context).load(uri).into(recentFoodImage)

                if (!uriString.isNullOrEmpty()) {
                    Glide.with(context).load(Uri.parse(uriString)).into(recentFoodImage)
                } else {
                    recentFoodImage.setImageResource(R.drawable.ic_launcher_background) // Use a placeholder image
                }

//                orderFoodPrice.text =  foodPrice[position]
            }
        }
    }

}