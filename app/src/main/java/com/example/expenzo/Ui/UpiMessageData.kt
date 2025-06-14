package com.example.expenzo.Ui

data class UpiMessageData(
    val Amount: String,
    val Bank: String,
    val Account: String,
    val Receiver: String,
    val Date: String,
    val UPIRefID: String
)