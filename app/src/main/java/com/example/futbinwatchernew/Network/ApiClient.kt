package com.example.futbinwatchernew.Network

import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceWrapperResponse
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.Services.Models.ClientTrackedPlayers
import retrofit2.http.*

interface ApiClient {
    @GET ("/search?extra=1&v=1")
    suspend fun searchPlayerNames(@Query("year") fifaVersion:Int,@Query("term") searchTerm:String ):List<SearchPlayerResponse>

    @GET("?platform=PS")
    suspend fun getCurrentPriceFor(@Query("ID")playerId:Int,@Query("platform")platform:Platform):PlayerPriceWrapperResponse


    @PUT("UpdateClientsTrackedPlayer/{clientId}")
    suspend fun postTrackedPlayersCondition(@Path("clientId") clientId:String,@Body playersCondition:List<ClientTrackedPlayers>)
}