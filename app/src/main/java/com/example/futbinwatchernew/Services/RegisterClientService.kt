package com.example.futbinwatchernew.Services

import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.Client
import com.example.futbinwatchernew.Utils.SharedPrefFileNames
import com.example.futbinwatchernew.Utils.SharedPrefRepo
import com.example.futbinwatchernew.Utils.SharedPrefsTags
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterClientService:FirebaseMessagingService() {
    lateinit var sharedPrefRepo:SharedPrefRepo
    @Inject
    lateinit var apiClient: ApiClient


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        FUTBINWatcherApp.component["SERVICE"]!!.inject(this)
        sharedPrefRepo = SharedPrefRepo(this, SharedPrefFileNames.CLIENT_REGISTRATION)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{task ->
            if(!task.isSuccessful){
                return@addOnCompleteListener
            }
            else{
                sharedPrefRepo.writeToSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY, newToken)
            }
        }
        addOrUpdateTokenOnServer(newToken)
    }


    private fun addOrUpdateTokenOnServer(newToken:String){
        val clientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
        CoroutineScope(Dispatchers.IO).launch {
            if(clientId == -1){
                val newClientId = addClient(Client(0,newToken))
                if(newClientId != 0){
                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID, newClientId)
                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, true)
                }
                else{
                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, false)
                }
            }
            else{
                if (editClient(clientId, newToken)){
                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, true)
                }
                else{
                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, false)
                }
            }
        }


    }


    private suspend fun addClient(client:Client):Int{
        var newClientId:Int
        try {
             newClientId = apiClient.postClient(client)
        }
        catch (e:Exception){
            newClientId = 0
        }
        return newClientId
    }

    private suspend fun editClient(clientId:Int, newToken: String):Boolean{
        var isSuccess:Boolean
        try {
            apiClient.putClient(clientId, Client(clientId,newToken))
            isSuccess = true
        }
        catch (e:Exception){
            isSuccess = false
        }
        return isSuccess
    }



}