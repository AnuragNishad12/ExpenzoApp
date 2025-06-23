package com.example.expenzo.Model

import com.google.gson.annotations.SerializedName

data class FetchCurrentDayDataModel30days(
    @SerializedName("userId")
    val userId : String
)

data class FetchCurrentDayDataResponse30days(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val UserData: List<FetchOtherCurrentDataResponse30days>
)

data class FetchOtherCurrentDataResponse30days(
    @SerializedName("_id")
    val id: String,

    @SerializedName("Account")
    val account: String,

    @SerializedName("Amount")
    val  Amount :String,

    @SerializedName("Bank")
    val bank : String,

    @SerializedName("Date")
    val Date: String,

    @SerializedName("Receiver")
    val Receiver : String,

    @SerializedName("UPIRefID")
    val UPIRefID: String
)