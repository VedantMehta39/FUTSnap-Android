package com.example.futbinwatchernew.UI.Models

import java.util.*


enum class Platform{
    PS,XB
}

data class PlayerDialogFragModel(val id:Int, val cardName:String, val imageURL:String,
                                 var currentPrice:EnumMap<Platform,Int?>, val isEdited:Boolean)