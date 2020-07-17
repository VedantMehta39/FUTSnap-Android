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
    }



}