package com.example.cardpro.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @SerializedName("card_name")
    val cardName: String? = null,
    @SerializedName("card_number")
    val cardNumber: String? = null,
	@SerializedName("expire_date")
    val expireDate: String? = null,
	@SerializedName("cvv")
    val cvv: Int? = null,
    @SerializedName("is_available")
    val isAvailable: Boolean = false

)

