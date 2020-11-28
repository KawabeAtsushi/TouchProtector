package com.pandatone.touchProtector

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object MyNotification {
    private const val CHANNEL_ID = "channel_id_touch_protector"
    private const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT
    private val ACTIVITY = MainActivity::class.java

    /**
     * Set the info for the views that show in the notification panel.
     */
    fun build(context: Context): Notification {
        // Create a notification channel
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.first_line),
                CHANNEL_IMPORTANCE
            )
        )

        // The PendingIntent to launch our activity if the user selects this notification
        val pendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, ACTIVITY), 0
        )

        val stopIntent = Intent(context, StopServiceBroadcastReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(context, 1, stopIntent, 0)

        return Notification.Builder(context, CHANNEL_ID)
            .setAutoCancel(false)  // don't dismiss when touched
            .setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
            .setContentTitle(context.getString(R.string.first_line))  // the label of the entry
            .setContentText(context.getString(R.string.second_line))  // the contents of the entry
            .setSmallIcon(R.mipmap.ic_launcher_round)  // the status ic_cat
            .setTicker(context.getText(R.string.app_name))  // the status text
            .setWhen(System.currentTimeMillis())  // the time stamp
            .addAction(0, context.getString(R.string.stop), stopPendingIntent)
            .build()
    }
}
