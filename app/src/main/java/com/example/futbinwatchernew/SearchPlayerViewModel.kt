package com.example.futbinwatchernew

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceResponse
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.Services.Models.PlayerTrackingRequest
import com.example.futbinwatchernew.UI.Event
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SearchPlayerViewModel():ViewModel() {
    var searchPlayersResult = MutableLiveData<List<SearchPlayerResponse>>()
    @Inject lateinit var apiClient:ApiClient

    fun getSearchPlayerResults(fifaVersion:Int, searchTerm:String) {
        viewModelScope.launch {
            try {
                val results = apiClient.searchPlayerNames(fifaVersion, searchTerm)
                searchPlayersResult.value =  results
            }
            catch (e:Exception){
                throw e
            }
        }
    }








}