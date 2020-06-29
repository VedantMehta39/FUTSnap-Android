package com.example.futbinwatchernew.Database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<PlayerDBModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: PlayerDBModel)

    @Query("Delete from tracked_players where 1 = 1")
    suspend  fun deleteAll()

    @Delete
    suspend fun deletePlayer(player:PlayerDBModel)

    @Query("Select * from tracked_players")
    fun getTrackedPlayerList():LiveData<List<PlayerDBModel>>

}