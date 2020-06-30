package com.example.futbinwatchernew.Models

import java.util.*


enum class Platform{
    PS,XB
}

data class PlayerDialogFragModel(val id:Int, val name:String, val imageURL:String,
                                 val rating:Int,
                                 var currentPrice:EnumMap<Platform,Int>)