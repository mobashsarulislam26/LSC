package com.example.data.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CampFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Refreshed Token: $token")
        subscribeToDefaultTopics()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "🚨 জরুরি ক্যাম্প আপডেট (Urgent Camp Update)"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "ক্যাম্পের নতুন আপডেট বা সময়সূচী পরিবর্তন এসেছে।"

        val type = remoteMessage.data["type"] ?: "URGENT_UPDATE"

        showNotification(applicationContext, title, body, type)
    }

    companion object {
        private const val TAG = "CampFCMService"
        const val CHANNEL_URGENT_ID = "camp_urgent_updates"
        const val CHANNEL_SCHEDULE_ID = "camp_schedule_changes"

        fun subscribeToDefaultTopics() {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic("camp_updates")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "Subscribed to camp_updates topic")
                    }
                FirebaseMessaging.getInstance().subscribeToTopic("urgent_announcements")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "Subscribed to urgent_announcements topic")
                    }
                FirebaseMessaging.getInstance().subscribeToTopic("schedule_changes")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "Subscribed to schedule_changes topic")
                    }
            } catch (e: Exception) {
                Log.w(TAG, "FCM Topic subscription error: ${e.message}")
            }
        }

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val urgentChannel = NotificationChannel(
                    CHANNEL_URGENT_ID,
                    "জরুরি ক্যাম্প সতর্কতা (Urgent Camp Updates)",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "জরুরি ঘটনা, মেডিকেল অ্যালার্ট ও লাইফ স্কিলস ক্যাম্পের জরুরি খবর"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                }

                val scheduleChannel = NotificationChannel(
                    CHANNEL_SCHEDULE_ID,
                    "সময়সূচী পরিবর্তন (Schedule Changes)",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "ক্যাম্প সেশন, খাবারের সময়সূচী ও ইভেন্টের রিয়েল-টাইম আপডেট"
                    enableVibration(true)
                }

                notificationManager.createNotificationChannel(urgentChannel)
                notificationManager.createNotificationChannel(scheduleChannel)
            }
        }

        fun showNotification(
            context: Context,
            title: String,
            body: String,
            type: String = "URGENT_UPDATE"
        ) {
            createNotificationChannels(context)

            val channelId = if (type == "SCHEDULE_CHANGE") CHANNEL_SCHEDULE_ID else CHANNEL_URGENT_ID

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NOTIFICATION_TYPE", type)
                putExtra("NOTIFICATION_TITLE", title)
                putExtra("NOTIFICATION_BODY", body)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationId = (System.currentTimeMillis() % 10000).toInt()
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
}
