package com.example.futbinwatchernew.Network

import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceWrapperResponse
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET ("/search?extra=1&v=1")
    suspend fun searchPlayerNames(@Query("year") fifaVersion:Int,@Query("term") searchTerm:String ):List<SearchPlayerResponse>

    @GET("?platform=PS")
    suspend fun getCurrentPriceFor(@Query("ID")playerId:Int):PlayerPriceWrapperResponse
}