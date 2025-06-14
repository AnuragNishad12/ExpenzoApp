package com.example.expenzo.Utils

import com.example.expenzo.Ui.UpiMessageData


fun parseUpiDataFromMessage(message: String): UpiMessageData? {
    val regex = Regex(
        """Sent\s+Rs\.?(\d+\.?\d{0,2})\s+from\s+([A-Za-z\s]+)\s+AC\s+([Xx\d]+)\s+to\s+([\w\d\-@.]+)\s+on\s+(\d{2}-\d{2}-\d{2}).*?UPI Ref\s+(\d{10,})""",
        RegexOption.IGNORE_CASE
    )

    val match = regex.find(message)
    return match?.let {
        UpiMessageData(
            Amount = "₹${it.groupValues[1]}",
            Bank = it.groupValues[2].trim(),
            Account = it.groupValues[3],
            Receiver = it.groupValues[4],
            Date = it.groupValues[5],
            UPIRefID = it.groupValues[6]
        )
    }
}