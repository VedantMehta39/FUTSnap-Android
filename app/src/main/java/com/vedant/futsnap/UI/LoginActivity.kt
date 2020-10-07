package com.vedant.futsnap.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.vedant.futsnap.FUTSnapApp
import com.vedant.futsnap.Network.ResponseModels.Client
import com.vedant.futsnap.R
import com.vedant.futsnap.UI.ErrorHandling.ErrorHandling
import com.vedant.futsnap.UI.ViewModels.CustomViewModelFactory
import com.vedant.futsnap.Utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginActivity: AppCompatActivity()  {
    private val RC_SIGN_IN = 4040
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory
    @Inject
    lateinit var clientUtility: ClientUtility

    lateinit var errorHandler:ErrorHandling

    lateinit var googleSignInButton: SignInButton

    lateinit var iv_app_logo:ImageView

    lateinit var spinner:ProgressBar

    lateinit var sharedPrefRepo: SharedPrefRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        iv_app_logo = findViewById(R.id.app_logo)
        googleSignInButton = findViewById(R.id.google_sign_in_button)
        spinner = findViewById(R.id.loading_spinner)
        auth = FirebaseAuth.getInstance()
        googleSignInButton.setOnClickListener{
            signIn()
            spinner.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        FUTSnapApp.component.inject(this)
        errorHandler = ErrorHandling(this,
            supportFragmentManager,
            clientUtility::addOrUpdateTokenOnServer)
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        sharedPrefRepo = SharedPrefRepo(this, SharedPrefFileNames.CLIENT_REGISTRATION)
        if(auth.currentUser != null &&
            sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) != 0){
            updateUI(false)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }
            catch (e: Exception){
                Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val client:Client?
                    val loggedInUserEmail = auth.currentUser!!.email!!

                    val firebaseToken = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY) as String?
                    if(task.result!!.additionalUserInfo!!.isNewUser){
                        client = Client(0, loggedInUserEmail,firebaseToken!!,null)
                    }
                    else{
                        val loggedInUserClientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
                        if (loggedInUserClientId == 0){
                            runBlocking {
                                withContext(Dispatchers.IO){
                                    val response = clientUtility
                                        .getClientByEmail(loggedInUserEmail)
                                    when(response){
                                        is NetworkResponse.Success -> {
                                            val oldClient = response.data
                                            client = Client(oldClient.Id,oldClient.Email,firebaseToken!!,null)
                                            sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID,client.Id)
                                        }
                                        is NetworkResponse.Failure ->{
                                            client = null
                                            auth.currentUser!!.delete()
                                            errorHandler.handle(response.error)
                                        }
                                    }
                                }
                            }

                        }
                        else{
                            client = Client(loggedInUserClientId,loggedInUserEmail,firebaseToken!!,null)
                        }
                    }

                    client?.let {
                        clientUtility.addOrUpdateTokenOnServer(it).observe(this, Observer {response ->
                            when (response) {
                                is NetworkResponse.Success -> {
                                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID, response.data)
                                    updateUI(task.result!!.additionalUserInfo!!.isNewUser)
                                }
                                is NetworkResponse.Failure -> {
                                    spinner.visibility = View.GONE
                                    iv_app_logo.visibility = View.GONE
                                    googleSignInButton.visibility = View.GONE
                                    errorHandler.handle(response.error)
                                    auth.currentUser!!.delete()
                                }

                            }
                        })
                    }
                }
                else{
                    Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun updateUI(isNewUser:Boolean){
        val nextIntent = Intent(this,MainActivity::class.java)
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        nextIntent.putExtra("FROM","LOGIN_ACTIVITY")
        if(intent.getStringExtra("TO") != null){
            nextIntent.putExtra("TO", "TRACKED_PLAYERS_FRAGMENT")
        }
        else{
            if(isNewUser){
                nextIntent.putExtra("TO","NOTIFICATION_PROBLEM_INFO_FRAGMENT")
            }
        }
        startActivity(nextIntent)
        finish()
    }
}