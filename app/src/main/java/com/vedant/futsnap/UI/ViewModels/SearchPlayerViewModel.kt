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
                val transformedResults = mutableListOf<SearchPlayerResponse>()
                results.forEach {
                    transformedResults.add(
                        SearchPlayerResponse(getPlayerIdFromImageUrl(it.playerImage),it.playerName,
                            it.playerRating, it.playerImage))
                }
                searchPlayersResult.value =  transformedResults
            }
            catch (e:Exception){
                error.value = Event(Error.GeneralError("Couldn't find any players or couldn't connect to the " +
                        "servers. Please try again!"))
            }
        }
    }

    private fun getPlayerIdFromImageUrl(url:String): Int {
        val endIndex = url.length - 1
        var startRecording = false
        var idImage = ""
        for (i in endIndex downTo 0 step 1){
            if (startRecording){
                if (url[i] == '/'){
                    break
                }
                idImage = url[i] + idImage
            }
            else if(url[i] == '?'){
                startRecording = true
            }
        }

        val idImageLen = idImage.length

        return idImage.substring(IntRange(0,idImageLen- 1 - 4 )).toInt()


    }








}