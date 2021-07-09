package com.pquinonezv.ejemplosfirebase

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private var btnCreateAccount : Button? = null
    private var btnSignIn : Button? = null
    private var btnForgottenPassword : Button? = null
    private var editEmail : EditText? = null
    private var editPassword : EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnForgottenPassword = findViewById(R.id.btnforgottenPassword)
        editEmail = findViewById(R.id.etEmail)
        editPassword = findViewById(R.id.etPassword)

        initializeFirebase()

        btnCreateAccount?.setOnClickListener(View.OnClickListener {
            createAccount(this.editEmail?.getText().toString(), editPassword?.getText().toString())
        })

        btnSignIn?.setOnClickListener(View.OnClickListener {
            signInAccount(this.editEmail?.getText().toString(), editPassword?.getText().toString())
        })

        btnForgottenPassword
            ?.setOnClickListener(View.OnClickListener {
                PasswordForgotten(this.editEmail?.getText().toString())
        })

    }

    private fun initializeFirebase() {
        firebaseAuth = Firebase.auth

        authStateListener = FirebaseAuth.AuthStateListener { it ->
            var firebaseUser: FirebaseUser? = it.currentUser
            if( firebaseUser!=null ) {
                Log.w(
                    TAG,
                    "onAuthStateChanged - signedIn " + firebaseUser.uid + " " + firebaseUser.email
                )
            } else {
                Log.w(TAG, "onAuthStateChanged - signed_out ")
            }

        }
    }

    private fun createAccount(email: String, password: String) {
        firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(
            this,
            OnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Create Account Success!!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Create Account Unsuccess!!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun PasswordForgotten(emailAddress: String) {

        firebaseAuth?.sendPasswordResetEmail(emailAddress)
            ?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent to " + emailAddress, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Email sent.")
                }
            })

    }

    private fun signInAccount(email: String, password: String) {
        firebaseAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(
            this,
            OnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "SignIn Account Success!!", Toast.LENGTH_SHORT).show()
                    var intent: Intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "SignIn Account Unsuccess!!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onStart() {
        super.onStart()

        authStateListener?.let { firebaseAuth?.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        authStateListener?.let { firebaseAuth?.removeAuthStateListener(it) }
    }
}