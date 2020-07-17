package com.example.futbinwatchernew

import androidx.lifecycle.*
import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.UI.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceResponse
import com.example.futbinwatchernew.UI.Event
import kotlinx.coroutines.launch
import com.example.futbinwatchernew.Utils.Error
import java.lang.Exception
import javax.inject.Inject

class SinglePlayerDialogFragmentViewModel:ViewModel() {

    @Inject lateinit var apiClient:ApiClient
    var error = MutableLiveData<Error>()
    var selectedPlayer = MutableLiveData<Event<PlayerDialogFragModel>>()


    fun initSelectedPlayer(data: PlayerDialogFragModel){
        viewModelScope.launch {
            try {
                data.currentPrice[Platform.PS] = getPlayerCurrentPrice(data.id, Platform.PS)
                data.currentPrice[Platform.XB] = getPlayerCurrentPrice(data.id, Platform.XB)
                selectedPlayer.value = Event(data)
            }
            catch (e:Exception){
                error.value = Error.GeneralError("Couldn't fetch the price from FUTBIN." +
                        " Please try again later!")
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