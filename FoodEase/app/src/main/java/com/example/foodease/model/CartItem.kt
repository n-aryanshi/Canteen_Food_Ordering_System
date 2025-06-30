package com.example.foodease.model

data class CartItem(
    var foodName: String?= null,
    var foodPrice: String?= null,
    var foodImage: String?= null,
    var foodQuantity: Int?= null,
    var foodDescription: String?= null,
    val foodIngredient: String? = null
)
