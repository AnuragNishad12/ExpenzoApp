package com.example.expenzo.Model

import com.google.gson.annotations.SerializedName

data class TransactionDeleteModel (

    @SerializedName("userId")
    val userId: String,

)

data class TransactionDeleteReponse(
    @SerializedName("status")
    val Status: String,

    @SerializedName("message")
    val message:String
)