package com.example.futbinwatchernew.UI.ViewModels

import androidx.lifecycle.*
import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.UI.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceResponse
import com.example.futbinwatchernew.UI.Event
import kotlinx.coroutines.launch
import com.example.futbinwatchernew.UI.ErrorHandling.Error
import java.lang.Exception

class SinglePlayerDialogFragmentViewModel(var apiClient:ApiClient):ViewModel() {

    var error = MutableLiveData<Event<Error>>()
    var selectedPlayer = MutableLiveData<Event<PlayerDialogFragModel>>()

    fun initSelectedPlayer(data: PlayerDialogFragModel){
        viewModelScope.launch {
            try {
                data.currentPrice[Platform.PS] = getPlayerCurrentPrice(data.id, Platform.PS)
                data.currentPrice[Platform.XB] = getPlayerCurrentPrice(data.id, Platform.XB)
                selectedPlayer.value = Event(data)
            }
            catch (e:Exception){
                error.value = Event(Error.GeneralError("Couldn't fetch the price from FUTBIN." +
                        " Please try again later!"))
            }

        }

    }

    private suspend fun getPlayerCurrentPrice(playerId:Int, platform: Platform):Int?{
        val similarPlayers:List<PlayerPriceResponse> =
            apiClient.getCurrentPriceFor(playerId,platform).data
        return similarPlayers.find {
            it.id == playerId
        }?.price

    }


}