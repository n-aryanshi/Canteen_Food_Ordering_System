package com.example.v2

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.v2.Model.AllMenu
import com.example.v2.databinding.ActivityAddMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddMenuActivity : AppCompatActivity() {

    //foodItems details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredients: String
    private var foodImageUri: Uri? = null

    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding: ActivityAddMenuBinding by lazy{
        ActivityAddMenuBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //initialize firebase
        auth = FirebaseAuth.getInstance()

        //initialize firebase database instance
        database = FirebaseDatabase.getInstance()

        binding.AddItemButton.setOnClickListener{
            foodName = binding.fooodName.text.toString().trim()
            foodPrice = binding.fooodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredients = binding.ingredients.text.toString().trim()

            if( !(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngredients.isBlank())){
                uploadData()
                Toast.makeText(this, "Item added succesfully", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "fill all the details ", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectImage.setOnClickListener{
            pickImage.launch("image/*")
        }

        binding.backBtn.setOnClickListener{
            finish()
        }
    }

    private fun uploadData() {
//        val currentAdminId = auth.currentUser?.uid
//
//        if (currentAdminId == null) {
//            Toast.makeText(this, "Admin not logged in", Toast.LENGTH_SHORT).show()
//            return
//        }

        // Reference to the new structure: admins/{adminId}/menu
        //val menuRef = database.getReference("admin").child(currentAdminId).child("menu")

        //get a reference to the menu node in the database
        val menuRef = database.getReference("menu")

        //generate a unique key for the menu item
        val newItemKey = menuRef.push().key

        if(foodImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener{
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    //create a new menu item
                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredients,
                        foodImage = downloadUrl.toString()
                    )
                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "data uploaded successfully", Toast.LENGTH_SHORT)
                                .show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "data uploading failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                }.addOnFailureListener{
                        Toast.makeText(this, "image uploading failed", Toast.LENGTH_SHORT).show()
                }

            }else{
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }


        }

    private val pickImage =registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.selectedImage.setImageURI(uri)
            foodImageUri = uri

        }
    }
}