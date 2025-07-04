package com.example.foodease.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.foodease.databinding.NotificationItemBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.foodease.R

class NotificationAdapter(private var notificationText:ArrayList<String>,
    private var notificationImage:ArrayList<Int>)
    : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = notificationText.size

    inner class NotificationViewHolder(private val binding: NotificationItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.apply {
                notificationTextView.text = notificationText[position]
                notificationImageView.setImageResource(notificationImage[position])
            }
        }
    }
}