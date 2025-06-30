package com.example.v2.Model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


class FoodDetails() : Serializable {

    var userUid: String? = null
    var userName: String? = null
//    var foodNames: MutableList<String>? = null
//    var foodImages: MutableList<String>? = null
//    var foodPrices: MutableList<String>? = null
//    var foodQuantities: MutableList<Int>? = null

    var foodName: List<String>? = null
    var foodImages: List<String>? = null
    var foodQuantities: List<Int>? = null
    var foodPrices: List<String>? = null
    var enrollment: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var orderDispatched: Boolean = false
    var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

//    constructor(parcel: Parcel) : this() {
//        userUid = parcel.readString()
//        userName = parcel.readString()
//
//        foodNames = parcel.createStringArrayList()
//        foodImages = parcel.createStringArrayList()
//        foodPrices = parcel.createStringArrayList()
//        foodQuantities = mutableListOf<Int>().apply {
//            val size = parcel.readInt()
//            repeat(size) {
//                add(parcel.readInt())
//            }
//        }
//
//        address = parcel.readString()
//        totalPrice = parcel.readString()
//        phoneNumber = parcel.readString()
//        orderAccepted = parcel.readByte()!= 0.toByte()
//        paymentReceived = parcel.readByte() != 0.toByte()
//        itemPushKey = parcel.readString()
//        currentTime = parcel.readLong()

//    }

//   fun writeToParcel(parcel: Parcel, flags: Int) {
//
//        parcel.writeString(userUid)
//        parcel.writeString(userName)
//        parcel.writeStringList(foodNames)
//        parcel.writeStringList(foodImages)
//        parcel.writeStringList(foodPrices)
//        if (foodQuantities != null) {
//            parcel.writeInt(foodQuantities!!.size)
//            foodQuantities!!.forEach { parcel.writeInt(it) }
//        } else {
//            parcel.writeInt(0)
//        }
//        parcel.writeString(address)
//        parcel.writeString(totalPrice)
//        parcel.writeString(phoneNumber)
//        parcel.writeByte(if (orderAccepted) 1 else 0)
//        parcel.writeByte(if (paymentReceived) 1 else 0)
//        parcel.writeString(itemPushKey)
//        parcel.writeLong(currentTime)
//
//   }
//
//    fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<FoodDetails> {
//        override fun createFromParcel(parcel: Parcel): FoodDetails {
//            return FoodDetails(parcel)
//        }
//
//        override fun newArray(size: Int): Array<FoodDetails?> {
//            return arrayOfNulls(size)
//        }
//    }
}