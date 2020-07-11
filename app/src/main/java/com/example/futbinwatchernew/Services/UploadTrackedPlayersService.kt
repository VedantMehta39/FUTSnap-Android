package com.example.futbinwatchernew.Services

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LifecycleService

import com.example.futbinwatchernew.Database.PlayerDAO
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Services.Models.Player
import com.example.futbinwatchernew.Services.Models.PlayerTrackingRequest
import com.example.futbinwatchernew.Utils.SharedPrefFileNames
import com.example.futbinwatchernew.Utils.SharedPrefRepo
import com.example.futbinwatchernew.Utils.SharedPrefsTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class UploadTrackedPlayersService:LifecycleService() {
    @Inject
    lateinit var playerDao:PlayerDAO
    @Inject
    lateinit var apiClient:ApiClient
    lateinit var sharedPref:SharedPreferences

    override fun onCreate() {
        FUTBINWatcherApp.component["SERVICE"]!!.inject(this)
        sharedPref= getSharedPreferences("FirebaseToken", Context.MODE_PRIVATE)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var isClientSuccessfullyRegistered = false
        val sharedPrefRepo = SharedPrefRepo(this, SharedPrefFileNames.CLIENT_REGISTRATION)
        while (!isClientSuccessfullyRegistered){
            if(sharedPrefRepo.readFromSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY) != null &&
                sharedPrefRepo.readFromSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC) == true){
                isClientSuccessfullyRegistered = true
            }
        }
        val clientId = sharedPref.getInt("CLIENT_ID", -1)

        val postData = intent!!.getParcelableArrayListExtra<PlayerDBModel>("POST_DATA")!!
        val putData = intent.getParcelableArrayListExtra<PlayerDBModel>("PUT_DATA")!!
        val deleteData = intent.getParcelableArrayListExtra<PlayerDBModel>("DELETE_DATA")!!

        if(postData.isNotEmpty()){
            createPlayerTrackingRequests(postData, clientId)
        }
        if(putData.isNotEmpty()){
            editPlayerTrackingRequests(putData,clientId)
        }
        if(deleteData.isNotEmpty()){
            deletePlayerTrackingRequests(deleteData,clientId)

        }
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createPlayerTrackingRequests(players:ArrayList<PlayerDBModel>, clientId:Int){

        val uploadRequests = players.map {
            PlayerTrackingRequest(it.futbinId, it.platform!!.ordinal,it.gte,it.lt,clientId,it.targetPrice,
                null, Player(it.futbinId,it.name + " " + it.rating.toString())) }.toList()

        CoroutineScope(Dispatchers.IO).launch {
            apiClient.postPlayerTrackingRequests(uploadRequests)
        }

    }

    private fun editPlayerTrackingRequests(players:ArrayList<PlayerDBModel>, clientId:Int){
        val uploadRequests = players.map {
            PlayerTrackingRequest(it.futbinId, it.platform!!.ordinal,it.gte,it.lt,clientId,it.targetPrice,
                null, null) }.toList()

        CoroutineScope(Dispatchers.IO).launch {
            apiClient.putPlayerTrackingRequests(clientId,uploadRequests)
        }
    }

    private fun deletePlayerTrackingRequests(players:ArrayList<PlayerDBModel>, clientId:Int){

        CoroutineScope(Dispatchers.IO).launch {
            players.forEach {
                apiClient.deletePlayerTrackingRequests(it.futbinId,clientId)
            }
        }
    }




}