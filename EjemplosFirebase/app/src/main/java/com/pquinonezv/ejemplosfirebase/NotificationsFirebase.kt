package com.pquinonezv.ejemplosfirebase

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class NotificationsFirebase : AppCompatActivity() {
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_firebase)
        notifications()
        //FirebaseMessaging.getInstance().subscribeToTopic("Pruebas")
        var service  = MyFirebaseMessagingService()


    }

    private fun notifications() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener{
            task ->
            if (!task.isSuccessful) {
                Log.w("NOTIFICACIONES", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            println("Este es el token de este dispositivo: "+token)

        })
    }
}