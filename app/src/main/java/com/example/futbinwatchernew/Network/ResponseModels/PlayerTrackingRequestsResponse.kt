package com.example.futbinwatchernew.Network.ResponseModels

data class Client(val Id:Int, val ClientToken: String, var MaxNumberOfRequests:Int?)

data class Player(val PlayerId:Int, val CardName:String, val ImageUrl:String)


data class PlayerTrackingRequest(var PlayerId:Int,
                                 var Platform: Int,
                                 var Gte:Boolean,
                                 var Lt:Boolean,
                                 var ClientId:Int,
                                 var TargetPrice:Int,
                                 var Client: Client?,
                                 var Player: Player?){
    constructor():this(0,-1,false,false,-1,-1,null,null)
}