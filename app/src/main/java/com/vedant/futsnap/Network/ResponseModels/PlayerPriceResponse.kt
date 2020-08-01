package com.vedant.futsnap.Network.ResponseModels

import com.google.gson.annotations.SerializedName

data class PlayerPriceResponse(@SerializedName("ID") val id:Int,@SerializedName("LCPrice") val price:Int)

data class PlayerPriceWrapperResponse(val data:List<PlayerPriceResponse>, val errorcode:String, val errormsg:String)