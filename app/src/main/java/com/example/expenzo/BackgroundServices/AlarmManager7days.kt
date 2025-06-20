package com.example.expenzo.BackgroundServices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmManager7days : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workRequest = OneTimeWorkRequestBuilder<Fetch7daysDataWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}