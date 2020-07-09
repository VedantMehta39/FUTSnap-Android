package com.example.futbinwatchernew.Database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse

@Entity(tableName = "tracked_players")
data class PlayerDBModel(@PrimaryKey(autoGenerate = true) var id:Int, var futbinId:Int,
                         var name:String, var imageURL:String,
                         var rating:Int, var targetPrice:Int,
                         var platform: Platform?, var gte:Boolean, var lt:Boolean,
                         @Ignore var isEdited:Boolean):Parcelable{


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        Platform.values()[parcel.readInt()],
        parcel.readInt() == 1,
        parcel.readInt() == 1,
        false
    )

    constructor(data:SearchPlayerResponse): this(0,data.id, data.playerName, data.playerImage,
        data.playerRating, -1, null,false,false,false )

    constructor():this(0,-1,"","",-1,-1,null,false,false,false)

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeInt(id)
        parcel.writeInt(futbinId)
        parcel.writeString(name)
        parcel.writeString(imageURL)
        parcel.writeInt(rating)
        parcel.writeInt(targetPrice)
        parcel.writeInt(platform!!.ordinal)
        parcel.writeInt(if(gte) 1 else 0)
        parcel.writeInt(if(lt) 1 else 0)
    }
    override fun describeContents(): Int {
        return -1
    }

    companion object CREATOR : Parcelable.Creator<PlayerDBModel> {
        override fun createFromParcel(parcel: Parcel): PlayerDBModel {
            return PlayerDBModel(parcel)
        }

        override fun newArray(size: Int): Array<PlayerDBModel?> {
            return arrayOfNulls(size)
        }
    }
}

