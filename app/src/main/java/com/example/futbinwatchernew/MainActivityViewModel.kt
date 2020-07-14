package com.example.futbinwatchernew

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class MainActivityViewModel: ViewModel() {

    @Inject
    lateinit var apiClient: ApiClient

    var clientId:Int = 0


    var allPlayerTrackingRequests = MutableLiveData<List<PlayerTrackingRequest>>()
    var deletedTrackedPlayers = ArrayList<PlayerTrackingRequest>()


    fun getPlayerTrackingRequests(){
        viewModelScope.launch {
            try{
                val data = apiClient.getPlayerTrackingRequests(clientId)
                allPlayerTrackingRequests.value = data
            }
            catch (e: Exception){
                throw e
            }
        }
    }

    fun addPlayerTrackingRequest(data: PlayerTrackingRequest){
        viewModelScope.launch {
            try{
                apiClient.postPlayerTrackingRequests(data)
                if(allPlayerTrackingRequests.value != null){
                    val currentData = (allPlayerTrackingRequests.value as MutableList)
                    currentData.add(data)
                    allPlayerTrackingRequests.value = currentData
                }
                else{
                    allPlayerTrackingRequests.value = listOf(data)
                }

            }
            catch (e:Exception){
                allPlayerTrackingRequests.value = emptyList()
                throw e
            }
        }
    }

    fun editPlayerTrackingRequest(playerId:Int, data: PlayerTrackingRequest){
        viewModelScope.launch {
            try{
                apiClient.putPlayerTrackingRequests(playerId, clientId, data)
                if(allPlayerTrackingRequests.value != null){
                    val currentData = (allPlayerTrackingRequests.value as MutableList)
                    currentData.removeIf { req -> req.PlayerId == playerId}
                    currentData.add(data)
                    allPlayerTrackingRequests.value = currentData
                }
                else{
                    allPlayerTrackingRequests.value = listOf(data)
                }
            }
            catch (e: Exception){
                allPlayerTrackingRequests.value = emptyList()
                throw e
            }
        }
    }
}