package com.example.futbinwatchernew.Database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse

@Entity(tableName = "tracked_players")
data class PlayerDBModel(@PrimaryKey var id:Int, var name:String, var imageURL:String,
                         var rating:Int, var targetPrice:Int,
                         var currentPrice:Int, var platform: Platform?, var gte:Boolean, var lt:Boolean){

    constructor(data:SearchPlayerResponse, currentPlayerPrice: Int): this(data.id, data.playerName, data.playerImage,
        data.playerRating, -1, currentPlayerPrice, null,false,false )

    constructor():this(-1,"","",-1,-1,-1,null,false,false)

}