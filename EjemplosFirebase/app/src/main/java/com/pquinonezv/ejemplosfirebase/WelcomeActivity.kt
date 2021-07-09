package com.pquinonezv.ejemplosfirebase

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private var tvUserDetail : TextView? = null
    private var btnSignOut : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        tvUserDetail = findViewById(R.id.tvUserDetail)
        btnSignOut = findViewById(R.id.btnSignOut)

        initializeFirebase()

        btnSignOut?.setOnClickListener(View.OnClickListener {
            signOut()
        })
    }

    private fun initializeFirebase() {
        firebaseAuth = Firebase.auth

        authStateListener = FirebaseAuth.AuthStateListener {
                it ->
            var firebaseUser: FirebaseUser? = it.currentUser
            if( firebaseUser!=null ) {
                tvUserDetail?.setText("IDUser: "+ firebaseUser.uid + " Email: " + firebaseUser.email)
            } else {
                Log.w(ContentValues.TAG, "onAuthStateChanged - signed_out ")
            }

        }
    }

    override fun onStart() {
        super.onStart()

        authStateListener?.let { firebaseAuth?.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        authStateListener?.let { firebaseAuth?.removeAuthStateListener(it) }
    }

    private fun signOut() {
        firebaseAuth?.signOut()

        var intent : Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }

}