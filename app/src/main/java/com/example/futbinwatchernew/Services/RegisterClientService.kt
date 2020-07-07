package com.example.futbinwatchernew.Services

import android.content.Context
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Services.Models.Client
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterClientService:FirebaseMessagingService() {
    private val FIREBASE_TOKEN_KEY = "FIREBASE_TOKEN_KEY"
    private val CLIENT_ID = "CLIENT_ID"
    @Inject
    lateinit var apiClient: ApiClient

    override fun onCreate() {
        super.onCreate()
        FUTBINWatcherApp.component["SERVICE"]!!.inject(this)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
    }


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        val sharedPref = getSharedPreferences("FirebaseToken", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{task ->
            if(!task.isSuccessful){
                return@addOnCompleteListener
            }
            else{
                with(sharedPref.edit()){
                    putString(FIREBASE_TOKEN_KEY, newToken)
                    commit()
                }
            }
        }
        addOrUpdateTokenOnServer(newToken)
    }


    private fun addOrUpdateTokenOnServer(newToken:String){
        val sharedPref = getSharedPreferences("FirebaseToken", Context.MODE_PRIVATE)
        val clientId = sharedPref.getInt(CLIENT_ID, -1)
        CoroutineScope(Dispatchers.IO).launch {
            if(clientId == -1){
                val newClientId = apiClient.postClient(Client(0,newToken))
                with(sharedPref.edit()){
                    putInt(CLIENT_ID, newClientId)
                    commit()
                }
            }
            else{
                apiClient.putClient(clientId, Client(clientId,newToken))
            }
        }


    }
}