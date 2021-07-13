package com.pquinonezv.ejemplosfirebase

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfig : AppCompatActivity() {
    lateinit var remoteConfig : FirebaseRemoteConfig

    private lateinit var linearLayout : LinearLayout
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_config)

        linearLayout = findViewById(R.id.activity_remote)
        logo = findViewById(R.id.ivConfigImagen)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        var remoteSettings : FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .setFetchTimeoutInSeconds(1)
            .build()

        remoteConfig.setConfigSettingsAsync(remoteSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        setConfigurationView()

    }

    fun syncronizeData(view : View) {
        var cacheExpiration : Long = 0

        remoteConfig.fetchAndActivate() .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d("REMOTECONFIG", "Config params updated: $updated")
                Toast.makeText(this, "Fetch and activate succeeded",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fetch failed",
                    Toast.LENGTH_SHORT).show()
            }
            setConfigurationView()
        }
    }

    private fun setConfigurationView() {
        linearLayout.setBackgroundColor(Color.parseColor(remoteConfig.getString("colorBackground")))

        if( remoteConfig.getString("imageBackground").equals("happyFace"))
        {
            logo.setImageResource(R.drawable.ic_baseline_tag_faces_24)
        } else {
            logo.setImageResource(R.drawable.ic_baseline_face_24)
        }
    }
}