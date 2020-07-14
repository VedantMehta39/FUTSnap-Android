package com.example.futbinwatchernew

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MainActivityViewModel: ViewModel() {

    @Inject
    lateinit var apiClient: ApiClient

    var clientId:Int = 0
    var errorMessage = MutableLiveData<String>()

    var allPlayerTrackingRequests = MutableLiveData<List<PlayerTrackingRequest>>()
    var deletedTrackedPlayers = Stack<PlayerTrackingRequest>()


    fun getPlayerTrackingRequests(){
        viewModelScope.launch {
            try{
                allPlayerTrackingRequests.value = apiClient.getPlayerTrackingRequests(clientId)
            }
            catch (e: Exception){
                errorMessage.value = "Couldn't connect to service! Please try again later"
            }
        }
    }

    fun addPlayerTrackingRequest(data: PlayerTrackingRequest){
        viewModelScope.launch {
            try{
                apiClient.postPlayerTrackingRequests(data)
                if(allPlayerTrackingRequests.value != null){
                    val currentData = allPlayerTrackingRequests.value?.toMutableList()!!
                    currentData.add(data)
                    allPlayerTrackingRequests.value = currentData
                }
                else{
                    allPlayerTrackingRequests.value = listOf(data)
                }

            }
            catch (e:Exception){
                errorMessage.value = "Couldn't connect to service! Please try again later"
            }
        }
    }

    fun editPlayerTrackingRequest(playerId:Int, data: PlayerTrackingRequest){
        viewModelScope.launch {
            try{
                apiClient.putPlayerTrackingRequests(playerId, clientId, data)
                if(allPlayerTrackingRequests.value != null){
                    val currentData = allPlayerTrackingRequests.value?.toMutableList()!!
                    currentData.removeIf { req -> req.PlayerId == playerId}
                    currentData.add(data)
                    allPlayerTrackingRequests.value = currentData
                }
                else{
                    allPlayerTrackingRequests.value = listOf(data)
                }
            }
            catch (e: Exception){
                errorMessage.value = "Couldn't connect to service! Please try again later"
            }
        }
    }

    fun deletePlayerTrackingRequest(playerId: Int, clientId:Int){
        viewModelScope.launch {
            try {
                apiClient.deletePlayerTrackingRequests(playerId,clientId)
            }
            catch (e:Exception){
                errorMessage.value = "Couldn't connect to service! Please try again later"
            }
        }
    }
}