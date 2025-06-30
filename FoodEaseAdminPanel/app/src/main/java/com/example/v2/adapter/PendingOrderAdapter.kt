package com.example.v2.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.v2.Model.FoodDetails
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

import com.example.v2.databinding.PendingOrderItemBinding

class PendingOrderAdapter(
    private val context: Context,
    private val customerName:MutableList<String>,
    private val quantity:MutableList<String>,
    private val foodImage:MutableList<String>,
    private val itemClicked: OnItemClicked,
 ): RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

     interface OnItemClicked{
         fun onItemClickListener(position: Int)
         fun onItemAcceptClickListener(position: Int)
         fun onItemDispatchClickListener(position: Int)
         fun getOrderItem(position: Int): FoodDetails

     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerName.size



    inner class PendingOrderViewHolder(private val binding: PendingOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                val orderItem = itemClicked.getOrderItem(position)
                orderCustName.text = customerName[position]
                quantityNo.text = quantity[position]

                val uri = Uri.parse(foodImage[position])
                Glide.with(context)
                    .load(uri)
                    .into(orderImage)



                orderStatusButton.apply {
//                    if(!isAccepted){
//                        text = "Accept"
//                    }else{
//                        text = "Dispatch"
//                    }
//
//                    setOnClickListener {
//                        if (!isAccepted) {
//                            text = "Dispatch"
//                            isAccepted = true
//                            Toast.makeText(context, "Order is Accepted", Toast.LENGTH_SHORT).show()
//                        } else {
//                            customerName.removeAt(adapterPosition)
//                            notifyItemRemoved(adapterPosition)
//
//                            // Show a toast for confirmation
//                            Toast.makeText(context, "Order is Dispatched", Toast.LENGTH_SHORT).show()
//                            }
//                        }

                    text = if (orderItem.orderAccepted != true) "Accept" else "Dispatch"

                    setOnClickListener {
                        if (orderItem.orderAccepted != true) {
                            text = "Dispatch"
                            orderItem.orderAccepted = true
                            itemClicked.onItemAcceptClickListener(position)
                            Toast.makeText(context, "Order is Accepted", Toast.LENGTH_SHORT).show()
                        } else {
                            itemClicked.onItemDispatchClickListener(position)
                            customerName.removeAt(adapterPosition)
                            quantity.removeAt(adapterPosition)
                            foodImage.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            Toast.makeText(context, "Order is Dispatched", Toast.LENGTH_SHORT).show()
                        }
                    }

                    }

                itemView.setOnClickListener {
                        itemClicked.onItemClickListener(position)
                    }
                }
            }



    }

}