package com.vedant.futsnap.Network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.vedant.futsnap.UI.Models.Platform
import com.vedant.futsnap.Utils.StringFormatter
import java.lang.reflect.Type
import java.util.*

class PriceDeserialiser :JsonDeserializer<EnumMap<Platform, Int?>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): EnumMap<Platform, Int?> {
        val responseMap = json!!.asJsonObject
        val pricesMap = responseMap[responseMap.keySet().first()].asJsonObject["prices"].asJsonObject
        val prices = EnumMap<Platform, Int?>(Platform::class.java)
        pricesMap.keySet().forEach { platformName ->
            when(platformName){
                "xbox" -> {
                    prices[Platform.XB] = StringFormatter.getNumberFromLocaleFormattedString(
                        pricesMap[platformName].asJsonObject["LCPrice"].asString)
                }
                "ps" -> {
                    prices[Platform.PS] = StringFormatter.getNumberFromLocaleFormattedString(
                        pricesMap[platformName].asJsonObject["LCPrice"].asString)
                }
            }
        }
        return prices
    }
}