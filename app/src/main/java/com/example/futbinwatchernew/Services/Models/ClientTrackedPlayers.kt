package com.example.futbinwatchernew.Services.Models

data class ClientTrackedPlayers(val Id:Int, val Platform: Int, val Gte:Boolean,
                                    val Lt:Boolean, val ClientId:String, val TargetPrice:Int)