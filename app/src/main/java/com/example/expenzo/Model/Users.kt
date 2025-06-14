package com.example.expenzo.Model

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("fullname")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("UniqueName")
    val uniqueName: String,

    @SerializedName("mobilenumber")
    val mobileNumber: String
)

data class CreateUserResponse(
    val status: Boolean,
    val message: String
)
