package com.example.foodease.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodease.R
import com.example.foodease.adapter.NotificationAdapter
import com.example.foodease.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NotificationBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBottomBinding.inflate(layoutInflater, container, false)
        val notificationText = listOf("Your order has been Cancelled successfully", "Your Order is ready", "Congrats Your order has been Placed")
        val notificationImages = listOf(R.drawable.sad, R.drawable.happy, R.drawable.done)

        val adapter = NotificationAdapter(
            ArrayList(notificationText),
            ArrayList(notificationImages)
        )

        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter


        return binding.root
    }

    companion object {

    }
}