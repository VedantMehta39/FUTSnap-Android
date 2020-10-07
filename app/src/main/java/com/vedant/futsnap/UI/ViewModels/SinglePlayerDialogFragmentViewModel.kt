package com.vedant.futsnap.UI.ViewModels

import androidx.lifecycle.*
import com.vedant.futsnap.UI.Models.Platform
import com.vedant.futsnap.UI.Models.PlayerDialogFragModel
import com.vedant.futsnap.Network.ApiClient
import com.vedant.futsnap.UI.Event
import kotlinx.coroutines.launch
import com.vedant.futsnap.UI.ErrorHandling.Error
import java.lang.Exception
import java.util.*

class SinglePlayerDialogFragmentViewModel(var apiClient:ApiClient):ViewModel() {

    var error = MutableLiveData<Event<Error>>()
    var selectedPlayer = MutableLiveData<Event<PlayerDialogFragModel>>()

    fun initSelectedPlayer(data: PlayerDialogFragModel){
        viewModelScope.launch {
            try {
                data.currentPrice = getPlayerCurrentPrice(21,data.id)

                selectedPlayer.value = Event(data)
            }
            catch (e:Exception){
                error.value = Event(Error.GeneralError("Couldn't fetch the price from the servers." +
                        " Please try again later!"))
            }

        }

    }

    private suspend fun getPlayerCurrentPrice(fifaVersion:Int, playerId:Int): EnumMap<Platform, Int?> = apiClient.getCurrentPriceFor(fifaVersion, playerId)


}