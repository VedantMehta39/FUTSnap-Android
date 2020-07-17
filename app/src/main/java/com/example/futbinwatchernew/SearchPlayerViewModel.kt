package com.example.futbinwatchernew

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.Utils.Error
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SearchPlayerViewModel():ViewModel() {
    var searchPlayersResult = MutableLiveData<List<SearchPlayerResponse>>()
    var error = MutableLiveData<Error>()
    @Inject lateinit var apiClient:ApiClient

    fun getSearchPlayerResults(fifaVersion:Int, searchTerm:String) {
        viewModelScope.launch {
            try {
                val results = apiClient.searchPlayerNames(fifaVersion, searchTerm)
                searchPlayersResult.value =  results
            }
            catch (e:Exception){
                error.value = Error.GeneralError("Couldn't find any players or couldn't connect to " +
                        "FUTBIN Servers. Please try again!")
            }
        }
    }








}