package com.example.v2.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.v2.databinding.DeliveryInfoItemBinding

class DeliveryInfoAdapter(private val customerName:MutableList<String>,
                          private val moneyStatus:MutableList<Boolean>,
                          private val totalPrice: List<String>,
                          private val onPaymentStatusChange: (position: Int) -> Unit
):RecyclerView.Adapter<DeliveryInfoAdapter.DeliveryInfoViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveryInfoAdapter.DeliveryInfoViewHolder {
        val binding = DeliveryInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryInfoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DeliveryInfoAdapter.DeliveryInfoViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerName.size

    inner class DeliveryInfoViewHolder(private val binding: DeliveryInfoItemBinding) : RecyclerView.ViewHolder(binding.root){
       fun bind(position: Int){
           binding.apply{
               custName.text = customerName[position]
               totalPriceTextView.text = "${totalPrice[position]}"  // Show actual price
               if(moneyStatus[position] == true){
                   paymentStatus.text = "Received"
               }else{
                   paymentStatus.text = "Not Received"
               }


               val colorMap = mapOf(
                   true to Color.GREEN,
                   false to Color.RED
               )

               paymentStatus.setTextColor(colorMap[moneyStatus[position]]?:Color.BLACK)
               cv2.backgroundTintList = ColorStateList.valueOf(colorMap[moneyStatus[position]]?:Color.GREEN)

               cv2.setOnClickListener {
                   if (!moneyStatus[position]) {
                       // Call the lambda to update status and Firebase
                       onPaymentStatusChange(position)
                   }
               }
           }
       }

    }
}