package com.example.futbinwatchernew.Database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlayerDBModel::class], version = 1)
abstract class FUTBINWatcherDatabase:RoomDatabase() {

    abstract fun getPlayerDao():PlayerDAO
}