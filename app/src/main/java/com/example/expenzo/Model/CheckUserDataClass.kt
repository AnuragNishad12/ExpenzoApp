package com.example.expenzo.Model

import com.google.gson.annotations.SerializedName


data class CheckUserDataClass(
    @SerializedName("uniqueName") val uniqueInBody: String
)

data class checkUserResponse(
    val status: Boolean,
    val message : String
)