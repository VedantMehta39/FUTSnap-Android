package com.example.futbinwatchernew.Database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse

@Entity(tableName = "tracked_players")
data class PlayerDBModel(@PrimaryKey val id:Int, val name:String, val imageURL:String,
                         val rating:Int, var targetPrice:Int,
                         var currentPrice:Int){

    constructor(data:SearchPlayerResponse, currentPlayerPrice: Int): this(data.id, data.playerName, data.playerImage,
        data.playerRating, -1, currentPlayerPrice )

}