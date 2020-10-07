package com.vedant.futsnap.Network

import com.vedant.futsnap.Network.ResponseModels.SearchPlayerResponse
import com.vedant.futsnap.Network.ResponseModels.Client
import com.vedant.futsnap.Network.ResponseModels.PlayerTrackingRequest
import com.vedant.futsnap.UI.Models.Platform
import retrofit2.http.*
import java.util.*

interface ApiClient {
    @GET ("/search?extra=1&v=1")
    suspend fun searchPlayerNames(@Query("year") fifaVersion:Int,@Query("term") searchTerm:String ):List<SearchPlayerResponse>

    @GET("/{year}/playerPrices")
    suspend fun getCurrentPriceFor(@Path("year") fifaVersion:Int, @Query("player")playerId:Int):EnumMap<Platform, Int?>

    @GET("Clients/{email}")
    suspend fun getClientByEmail(@Path("email") email:String):Client

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