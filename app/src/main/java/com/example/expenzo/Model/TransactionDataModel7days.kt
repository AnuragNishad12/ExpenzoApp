package com.example.expenzo.Model

import com.google.gson.annotations.SerializedName

data class TransactionDataModel7days(
    @SerializedName("userUniqueName")
    val userId:String,

    @SerializedName("Account")
    val account: String,

    @SerializedName("Amount")
    val amount:String,

    @SerializedName("Bank")
    val bank: String,

    @SerializedName("Date")
    val date: String,

    @SerializedName("Receiver")
    val receiver: String,

    @SerializedName("UPIRefID")
    val upiRefId: String

)


data class TransactionResponse7days(
    val status: Boolean,
    val message: String,
    val data: TransactionData7days
)

data class TransactionData7days(
    val userId: String,
    val Account: String,
    val Amount: String,
    val Bank: String,
    val Date: String,
    val Receiver: String,
    val UPIRefID: String,
)
