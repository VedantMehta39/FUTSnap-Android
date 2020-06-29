package com.example.futbinwatchernew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbinwatchernew.Database.PlayerDAO
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.UI.Event
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SearchPlayerViewModel():ViewModel() {
    var searchPlayersResult = MutableLiveData<List<SearchPlayerResponse>>()
    var selectedPlayer = MutableLiveData<Event<PlayerDBModel>>()
    @Inject lateinit var apiClient:ApiClient
    var allTrackedPlayers = ArrayList<PlayerDBModel>()
    lateinit var trackedPlayersInDb : LiveData<List<PlayerDBModel>>
    var deletedPlayersStack = Stack<PlayerDBModel>()
    @Inject
    lateinit var playerDAO: PlayerDAO

    fun getSearchPlayerResults(fifaVersion:Int, searchTerm:String) {
        viewModelScope.launch {
                searchPlayersResult.value = apiClient.searchPlayerNames(fifaVersion, searchTerm)

        }
    }

    fun initSelectedPlayer(data:SearchPlayerResponse){
        viewModelScope.launch {
            val price = getPlayerCurrentPrice(data.id)
            selectedPlayer.value = Event(PlayerDBModel(data, price))
        }

    }

    private suspend fun getPlayerCurrentPrice(playerId:Int):Int{
        val similarPlayers = apiClient.getCurrentPriceFor(playerId).data
        return similarPlayers.find {
            it.id == playerId
        }!!.price
    }

    fun insert(players: List<PlayerDBModel>){
        viewModelScope.launch {
            kotlin.runCatching {
                playerDAO.insert(players)
            }
        }
    }


    fun deleteAllTrackedPlayersInDb() {
        viewModelScope.launch {
            kotlin.runCatching {
                playerDAO.deleteAll()
            }
        }
    }

    fun deletePlayer(player:PlayerDBModel){
        viewModelScope.launch {
            kotlin.runCatching {
                playerDAO.deletePlayer(player)
            }
        }
    }

    fun getTrackedPlayersList() {
        trackedPlayersInDb = playerDAO.getTrackedPlayerList()
    }

    fun isTrackedPlayersInDBInitialized() = this::trackedPlayersInDb.isInitialized




}