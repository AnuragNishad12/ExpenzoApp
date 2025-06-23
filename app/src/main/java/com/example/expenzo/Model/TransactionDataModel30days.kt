package com.example.expenzo.Model

import com.google.gson.annotations.SerializedName

data class TransactionDataModel30days(
    @SerializedName("userId")
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


data class TransactionResponse30days(
    val status: Boolean,
    val message: String,
    val data: TransactionData30days
)

data class TransactionData30days(
    val userId: String,
    val Account: String,
    val Amount: String,
    val Bank: String,
    val Date: String,
    val Receiver: String,
    val UPIRefID: String,
)
