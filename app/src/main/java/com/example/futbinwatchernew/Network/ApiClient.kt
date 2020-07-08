package com.example.futbinwatchernew.Network

import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceWrapperResponse
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.Services.Models.PlayerTrackingRequest
import retrofit2.http.*

interface ApiClient {
    @GET ("/search?extra=1&v=1")
    suspend fun searchPlayerNames(@Query("year") fifaVersion:Int,@Query("term") searchTerm:String ):List<SearchPlayerResponse>

    @GET("?platform=PS")
    suspend fun getCurrentPriceFor(@Query("ID")playerId:Int,@Query("platform")platform:Platform):PlayerPriceWrapperResponse


    @POST("PlayerTrackingRequests/{clientId}")
    suspend fun postPlayerTrackingRequests(@Path("clientId") clientId:Int,@Body trackingRequests:List<PlayerTrackingRequest>)

    @PUT("PlayerTrackingRequests/{clientId}")
    suspend fun putPlayerTrackingRequests(@Path("clientId") clientId:Int, @Body trackingRequests:List<PlayerTrackingRequest>)

    @DELETE("PlayerTrackingRequests/{playerId}/{clientId}")
    suspend fun deletePlayerTrackingRequests(@Path("playerId") playerId:Int, @Path("clientId") clientId:Int)
}