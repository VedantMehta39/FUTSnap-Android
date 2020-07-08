package com.example.futbinwatchernew.Services

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleService

import com.example.futbinwatchernew.Database.PlayerDAO
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Services.Models.Player
import com.example.futbinwatchernew.Services.Models.PlayerTrackingRequest
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
        val clientId = sharedPref.getInt("CLIENT_ID", 2)

        val postData = intent!!.getParcelableArrayListExtra<PlayerDBModel>("POST_DATA")!!
        val putData = intent.getParcelableArrayListExtra<PlayerDBModel>("PUT_DATA")!!
        val deleteData = intent.getParcelableArrayListExtra<PlayerDBModel>("DELETE_DATA")!!

        createPlayerTrackingRequests(postData, clientId)
        editPlayerTrackingRequests(putData,clientId)
        deletePlayerTrackingRequests(deleteData,clientId)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createPlayerTrackingRequests(players:ArrayList<PlayerDBModel>, clientId:Int){

        val uploadRequests = players.map {
            PlayerTrackingRequest(it.futbinId, it.platform!!.ordinal,it.gte,it.lt,clientId,it.targetPrice,
                null, Player(it.futbinId,it.name)) }.toList()

        CoroutineScope(Dispatchers.IO).launch {
            apiClient.postPlayerTrackingRequests(clientId, uploadRequests)
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