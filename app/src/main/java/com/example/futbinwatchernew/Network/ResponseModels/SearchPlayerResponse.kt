package com.example.futbinwatchernew.Network.ResponseModels

import com.google.gson.annotations.SerializedName

data class SearchPlayerResponse(
    @SerializedName("id") val id:Int,
    @SerializedName("full_name") val playerName:String,
    @SerializedName("rating") val playerRating:Int,
    @SerializedName("image") val playerImage:String
)