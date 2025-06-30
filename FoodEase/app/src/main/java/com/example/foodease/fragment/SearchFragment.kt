package com.example.foodease.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodease.R
import com.example.foodease.adapter.MenuAdapter
import com.example.foodease.databinding.FragmentSearchBinding
import androidx.appcompat.widget.SearchView
import com.example.foodease.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {
    //enable binding
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems =  mutableListOf<MenuItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

//    private val filteredMenuItemName = mutableListOf<String>()
//    private val filteredMenuItemPrice = mutableListOf<String>()
//    private val filteredMenuItemImage = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Manually set the query hint
        binding.searchView.queryHint = "What do you want to eat?"

        // Debug log to check if the query hint is being set
        Log.d("SearchViewDebug", "Query Hint: " + binding.searchView.queryHint.toString())

        retrieveMenuItem()
        setUpSearchView()

        return binding.root
    }

    private fun retrieveMenuItem() {
        //get database reference
        database = FirebaseDatabase.getInstance()

        val foodReference: DatabaseReference = database.reference.child("menu")
        foodReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let{
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun showAllMenu() {
        val filteredMenuItem = ArrayList(originalMenuItems)
        setAdapter(filteredMenuItem)
    }

    private fun setAdapter(filteredMenuItem: List<MenuItem>) {
        adapter = MenuAdapter(filteredMenuItem, requireContext())
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecyclerView.adapter = adapter

    }


    private fun setUpSearchView() {
//        binding.searchView.isIconified = false  // Force expand SearchView
//        binding.searchView.clearFocus() // Remove focus to show the hint

        (binding.searchView as androidx.appcompat.widget.SearchView).setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenuItems(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenuItems(newText ?: "")
                return true
            }
        })
    }

    private fun filterMenuItems(query: String) {

        val filteredMenuItems = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }

        setAdapter(filteredMenuItems)
    }

    companion object {

    }
}


