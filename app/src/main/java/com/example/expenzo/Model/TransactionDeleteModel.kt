package com.example.expenzo.Model

import com.google.gson.annotations.SerializedName

data class TransactionDeleteResponse(
    @SerializedName("status")
    val status: Boolean, // Changed from String to Boolean

    @SerializedName("message")
    val message: String,

    @SerializedName("deletedCount")
    val deletedCount: Int? = null // Optional field since it might not always be present
)

// 2. Request Model (this looks correct)
data class TransactionDeleteModel(
    @SerializedName("uniqueName")
    val userId: String
)