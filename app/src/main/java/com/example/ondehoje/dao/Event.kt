package com.example.ondehoje.dao

import android.os.Parcel
import android.os.Parcelable

open class Event(
    open var name: String = "",
    open var date: String = "",
    open var location: String = "",
    open var description: String = "",
    open var category: String = "",
    open var latitude: String = "",
    open var longitude: String = "",
    open var foto: String = "",
    open var turno: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(foto)
        parcel.writeString(turno)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}