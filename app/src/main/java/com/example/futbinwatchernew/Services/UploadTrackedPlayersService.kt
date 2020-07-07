package com.example.futbinwatchernew.Services

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer

import com.example.futbinwatchernew.Database.PlayerDAO
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.Network.ApiClient
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


    override fun onCreate() {
        FUTBINWatcherApp.component["SERVICE"]!!.inject(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val trackedPlayers = playerDao.getTrackedPlayerList()
        trackedPlayers.observe(this, Observer {
            insertIntoRemoteDatabase(it)
            stopSelf()
        } )
        return super.onStartCommand(intent, flags, startId)
    }

    private fun insertIntoRemoteDatabase(players:List<PlayerDBModel>){
        val sharedPref = getSharedPreferences("FirebaseToken", Context.MODE_PRIVATE)
        val clientId = sharedPref.getInt("CLIENT_ID", -1)
        val uploadPlayers = players.map {
            PlayerTrackingRequest(it.id, it.platform!!.ordinal,it.gte,it.lt,clientId,it.targetPrice,
                null, null) }.toList()
        CoroutineScope(Dispatchers.IO).launch {
            apiClient.putTrackedPlayersCondition("1",uploadPlayers)
        }
    }

}