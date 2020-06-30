package com.example.futbinwatchernew.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.android.parcel.TypeParceler

@Database(entities = [PlayerDBModel::class], version = 1)
@TypeConverters(PlatformTypeConverter::class)
abstract class FUTBINWatcherDatabase:RoomDatabase() {

    abstract fun getPlayerDao():PlayerDAO
}