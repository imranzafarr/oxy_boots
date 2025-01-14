package com.example.ecommerceapp.Models

import android.os.Parcel
import android.os.Parcelable


@Parcelize
data class Cart(
    val uid: String="",
    val image:String="",
    val name: String="",
    val description:String="",
    val quantity:String="",
    val price:String="",
    val size:String="",
    val color:String="",
    var selectedQuantity:Int=1
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(quantity)
        parcel.writeString(price)
        parcel.writeString(size)
        parcel.writeString(color)
        parcel.writeInt(selectedQuantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cart> {
        override fun createFromParcel(parcel: Parcel): Cart {
            return Cart(parcel)
        }

        override fun newArray(size: Int): Array<Cart?> {
            return arrayOfNulls(size)
        }
    }
}



