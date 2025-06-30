package com.example.foodease

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.foodease.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    //by lazy ensures that the UI elements are accessed only when they're needed, not before.
    private val binding: ActivityChooseLocationBinding by lazy{
        ActivityChooseLocationBinding.inflate(layoutInflater) // to inflating the layout, which means converting the XML layout into actual objects you can interact with in your code.
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //placing all the items (UI elements) on the screen for the user to see and interact with. It makes sure that everything from the layout is displayed correctly on the screen.
        val locationList = arrayOf("Amroha", "Ghaziabad", "Dehradun", "Haridwar", "Delhi", "Banaras", "Kanpur", "Haryana","Gajraula")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, locationList)

        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

    }
}