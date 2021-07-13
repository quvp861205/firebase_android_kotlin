package com.pquinonezv.ejemplosfirebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {



    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        println("Your token" + p0)


    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

         println("Mi mensaje: " + p0.notification?.title)

        var i : Intent = Intent(this, MainActivity::class.java)
   
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        var pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT)

        var uri : Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notificationBuilder : NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.com_facebook_button_icon)
            .setContentTitle(p0.notification?.title)
            .setContentText(p0.notification?.body)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(uri)
            .setContentIntent(pendingIntent)

        var notificationMananger : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationMananger.notify(0, notificationBuilder.build())
    }


}