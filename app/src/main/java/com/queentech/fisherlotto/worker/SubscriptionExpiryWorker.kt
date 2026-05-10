package com.queentech.fisherlotto.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.queentech.fisherlotto.App
import com.queentech.fisherlotto.MainActivity
import com.queentech.fisherlotto.R

class SubscriptionExpiryWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, App.CHANNEL_ID)
            .setContentTitle("구독 만료 임박")
            .setContentText("구독이 3일 후 만료됩니다. 연장하시겠습니까?")
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "subscription_expiry"
        private const val NOTIFICATION_ID = 1001
    }
}
