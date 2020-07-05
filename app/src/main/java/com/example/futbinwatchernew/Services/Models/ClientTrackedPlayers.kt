package com.example.futbinwatchernew.Services.Models

data class Client(val ClientId: String)

data class Player(val PlayerId:Int)


data class PlayerTrackingRequest(val PlayerId:Int, val Platform: Int, val Gte:Boolean,
                                    val Lt:Boolean, val ClientId:String, val TargetPrice:Int,
                                val Client:Client?, val Player:Player?)