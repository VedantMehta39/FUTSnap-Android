package com.example.futbinwatchernew

import androidx.lifecycle.*
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.Client
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import com.example.futbinwatchernew.Utils.Error
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MainActivityViewModel(clientId:Int): ViewModel() {

    @Inject
    lateinit var apiClient: ApiClient

    var clientId = MutableLiveData(clientId)

    var error = MutableLiveData<Error>()

    var allPlayerTrackingRequests = MutableLiveData<List<PlayerTrackingRequest>>()
    var deletedTrackedPlayers = Stack<PlayerTrackingRequest>()
    val requestSuccessful = MutableLiveData<Boolean>()


    fun getPlayerTrackingRequests(){
        viewModelScope.launch {
            try{
                allPlayerTrackingRequests.value = apiClient.getPlayerTrackingRequests(clientId.value!!)
            }
            catch (e: Exception){
                error.value = Error.ServerError()
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
                requestSuccessful.value = true
            }
            catch (e:Exception){

                when(e){
                    is HttpException -> {
                        if (e.code() == 409){
                            error.value = Error.GeneralError("Cannot track a duplicate player!")
                        }
                    }
                    else -> {
                        error.value = Error.ServerError("Could not connect to server. Player not added." +
                                " Please try again later!")
                    }
                }

            }
        }
    }

    fun editPlayerTrackingRequest(playerId:Int, data: PlayerTrackingRequest){
        viewModelScope.launch {
            try{
                apiClient.putPlayerTrackingRequests(playerId, clientId.value!!, data)
                if(allPlayerTrackingRequests.value != null){
                    val currentData = allPlayerTrackingRequests.value?.toMutableList()!!
                    currentData.removeIf { req -> req.PlayerId == playerId}
                    currentData.add(data)
                    allPlayerTrackingRequests.value = currentData
                }
                else{
                    allPlayerTrackingRequests.value = listOf(data)
                }
                requestSuccessful.value = true
            }
            catch (e: Exception){
                error.value = Error.ServerError("Could not connect to server. Player not edited." +
                        " Please try again later!")
            }
        }
    }

    fun deletePlayerTrackingRequest(playerId: Int){
        viewModelScope.launch {
            try {
                apiClient.deletePlayerTrackingRequests(playerId,clientId.value!!)
                requestSuccessful.value = true
            }
            catch (e:Exception){
                error.value = Error.ServerError()
            }
        }
    }

    fun addClient(client: Client){
        viewModelScope.launch {
            try {
                clientId.value = apiClient.postClient(client)
            }
            catch (e:Exception){
                error.value = Error.RegistrationError()
            }
        }
    }

    fun editClient(client: Client){
        viewModelScope.launch {
            try {
                apiClient.putClient(client.Id, client)
            }
            catch (e:Exception){
                error.value = Error.RegistrationError()
            }
        }
    }
}