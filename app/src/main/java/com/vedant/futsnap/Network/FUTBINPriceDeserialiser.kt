package com.vedant.futsnap.Network

import com.vedant.futsnap.Network.ResponseModels.PlayerPriceResponse
import com.vedant.futsnap.Network.ResponseModels.PlayerPriceWrapperResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class FUTBINPriceDeserialiser :JsonDeserializer<PlayerPriceWrapperResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PlayerPriceWrapperResponse {
        val similarPlayers = json!!.asJsonObject.get("data")
            .asJsonArray.map{player ->
            PlayerPriceResponse( player.asJsonObject.get("ID").asInt,player.asJsonObject.get("LCPrice").asInt) }
        return PlayerPriceWrapperResponse(similarPlayers,"","")
    }
}