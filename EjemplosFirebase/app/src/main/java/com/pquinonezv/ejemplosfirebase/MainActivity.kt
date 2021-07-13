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
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private var googleSignInClient: GoogleSignInClient? = null
    private var RC_SIGN_IN = 1

    private var callbackManager: CallbackManager? = null

    private var btnCreateAccount : Button? = null
    private var btnSignIn : Button? = null
    private var btnForgottenPassword : Button? = null
    private var btnSignInGoogle: SignInButton? = null
    private var btnSignInFacebook: LoginButton? = null
    private var btnRealtime: Button? = null
    private var btnStorage: Button? = null
    private var btnNotifications: Button? = null
    private var btn_remote_config: Button? = null


    private var editEmail : EditText? = null
    private var editPassword : EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnForgottenPassword = findViewById(R.id.btnforgottenPassword)
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle)
        btnSignInFacebook = findViewById(R.id.btnSignInFacebbok)
        btnRealtime = findViewById(R.id.btnRealtime)
        btnStorage = findViewById(R.id.btnStorage)
        btn_remote_config = findViewById(R.id.btnConfig)
        btnNotifications = findViewById(R.id.btnNotifications)


        editEmail = findViewById(R.id.etEmail)
        editPassword = findViewById(R.id.etPassword)

        initializeFirebase()

        //Crear cuenta con correo y contraseña
        btnCreateAccount?.setOnClickListener(View.OnClickListener {
            createAccount(this.editEmail?.getText().toString(), editPassword?.getText().toString())
        })

        //Loguearse con correo y contraseña
        btnSignIn?.setOnClickListener(View.OnClickListener {
            signInAccount(this.editEmail?.getText().toString(), editPassword?.getText().toString())
        })

        //Reestablecer contraseña olvidada
        btnForgottenPassword
            ?.setOnClickListener(View.OnClickListener {
                PasswordForgotten(this.editEmail?.getText().toString())
        })

        //Loguearse con google
        btnSignInGoogle?.setOnClickListener(View.OnClickListener {
            signInGoogle()
        })

        btnRealtime?.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(this, RealtimeDatabaseFirebase::class.java)
            startActivity(intent)

        })

        btnStorage?.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(this, FirebaseStorage::class.java)
            startActivity(intent)

        })

        btnNotifications?.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(this, NotificationsFirebase::class.java)
            startActivity(intent)

        })

        btn_remote_config?.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(this, RemoteConfig::class.java)
            startActivity(intent)

        })



    }

    //Inicializamos observer para la conexion con firebase y google
    private fun initializeFirebase() {
        firebaseAuth = Firebase.auth

        //inicializacion de login con email y contraseña
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

        //inicializacion de google account
        var gso : GoogleSignInOptions? = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = gso?.let { GoogleSignIn.getClient(this, it) }

        //inicializacion facebook
        callbackManager = CallbackManager.Factory.create();
        btnSignInFacebook?.setReadPermissions("email", "public_profile");

        btnSignInFacebook?.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d("LOG", result?.accessToken.toString())
                if (result != null) {
                    handleFacebookAccessToken(result.accessToken)
                }
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "facebook:onError", error)
            }

        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth?.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    //Crear nueva cuenta con corre y contraseña
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


    //Reestablecer contraseña
    private fun PasswordForgotten(emailAddress: String) {

        firebaseAuth?.sendPasswordResetEmail(emailAddress)
            ?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent to " + emailAddress, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Email sent.")
                }
            })

    }


    //logueo con email y contraseña
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

    //Agregamos observador para correo y contraseña
    override fun onStart() {
        super.onStart()

        authStateListener?.let { firebaseAuth?.addAuthStateListener(it) }
    }

    //Detebenis el observador por correo y contraseña
    override fun onStop() {
        super.onStop()
        authStateListener?.let { firebaseAuth?.removeAuthStateListener(it) }
    }

    //Ejecutamos el login con google
    private fun signInGoogle() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //Esperando respuesta del login por google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        callbackManager?.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    //Obtener la credencial de google ya logueado
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth?.currentUser
                    Log.w("GOOGLE", "Login with google " + user?.email)

                    var intent: Intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }
}