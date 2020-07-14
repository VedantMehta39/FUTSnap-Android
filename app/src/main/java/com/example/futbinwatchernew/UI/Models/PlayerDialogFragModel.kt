package com.example.futbinwatchernew.UI.Models

import java.util.*


enum class Platform{
    PS,XB
}

data class PlayerDialogFragModel(val id:Int, val cardName:String, val imageURL:String,
                                 var currentPrice:EnumMap<Platform,Int?>, val targetPrice:Int?,
                                 val platform:Platform, val gte:Boolean, val lt:Boolean,
                                 val isEdited:Boolean)