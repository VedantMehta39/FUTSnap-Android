package com.example.futbinwatchernew

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.UI.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceResponse
import com.example.futbinwatchernew.UI.Event
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SinglePlayerDialogFragmentViewModel:ViewModel() {

    @Inject lateinit var apiClient:ApiClient

    var selectedPlayer = MutableLiveData<Event<PlayerDialogFragModel>>()


    fun initSelectedPlayer(data: PlayerDialogFragModel){
        viewModelScope.launch {
            data.currentPrice[Platform.PS] = getPlayerCurrentPrice(data.id, Platform.PS)
            data.currentPrice[Platform.XB] = getPlayerCurrentPrice(data.id, Platform.XB)
            selectedPlayer.value = Event(data)
        }

    }

    private suspend fun getPlayerCurrentPrice(playerId:Int, platform: Platform):Int?{
        var similarPlayers:List<PlayerPriceResponse>
        try{
            similarPlayers = apiClient.getCurrentPriceFor(playerId,platform).data
        }
        catch (e: Exception){
            similarPlayers = emptyList()
        }
        return similarPlayers.find {
            it.id == playerId
        }?.price

    }


}