package com.example.expenzo.BackgroundServices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager


class MyAlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val workrequest = OneTimeWorkRequestBuilder<DeleteWorker>().build()
        WorkManager.getInstance(context).enqueue(workrequest)
    }

}