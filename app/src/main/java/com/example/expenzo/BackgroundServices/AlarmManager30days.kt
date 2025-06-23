package com.example.expenzo.BackgroundServices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmManager30days : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workRequest = OneTimeWorkRequestBuilder<Fetch30daysDataWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}