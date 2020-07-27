package com.example.futbinwatchernew.Network

import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceWrapperResponse
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.Network.ResponseModels.Client
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import retrofit2.http.*

interface ApiClient {
    @GET ("/search?extra=1&v=1")
    suspend fun searchPlayerNames(@Query("year") fifaVersion:Int,@Query("term") searchTerm:String ):List<SearchPlayerResponse>

    @GET("?platform=PS")
    suspend fun getCurrentPriceFor(@Query("ID")playerId:Int,@Query("platform")platform:Platform):PlayerPriceWrapperResponse

    @GET("Clients/")
    suspend fun getClient(@Query("Email") email:String):List<Client>

    @POST("Clients/")
    suspend fun postClient(@Body client: Client):Int

    @PUT("Clients/{clientId}")
    suspend fun putClient(@Path("clientId") clientId: Int, @Body client: Client):Int

    @GET("PlayerTrackingRequests/{clientId}")
    suspend fun getPlayerTrackingRequests(@Path("clientId") clientId: Int):List<PlayerTrackingRequest>

    @POST("PlayerTrackingRequests/")
    suspend fun postPlayerTrackingRequests(@Body trackingRequest: PlayerTrackingRequest):PlayerTrackingRequest

    @PUT("PlayerTrackingRequests/{playerId}/{clientId}")
    suspend fun putPlayerTrackingRequests(@Path("playerId") playerId:Int,@Path("clientId") clientId:Int, @Body trackingRequest: PlayerTrackingRequest)

    @DELETE("PlayerTrackingRequests/{playerId}/{clientId}")
    suspend fun deletePlayerTrackingRequests(@Path("playerId") playerId:Int, @Path("clientId") clientId:Int):PlayerTrackingRequest
}