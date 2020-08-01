package com.vedant.futsnap.UI.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedant.futsnap.Network.ApiClient
import com.vedant.futsnap.Network.ResponseModels.SearchPlayerResponse
import com.vedant.futsnap.UI.ErrorHandling.Error
import com.vedant.futsnap.UI.Event
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchPlayerViewModel(var apiClient:ApiClient):ViewModel() {
    var searchPlayersResult = MutableLiveData<List<SearchPlayerResponse>>()
    var error = MutableLiveData<Event<Error>>()

    fun getSearchPlayerResults(fifaVersion:Int, searchTerm:String) {
        viewModelScope.launch {
            try {
                val results = apiClient.searchPlayerNames(fifaVersion, searchTerm)
                searchPlayersResult.value =  results
            }
            catch (e:Exception){
                error.value = Event(Error.GeneralError("Couldn't find any players or couldn't connect to " +
                        "FUTBIN Servers. Please try again!"))
            }
        }
    }








}