package com.vedant.futsnap.UI.ViewModels

import androidx.lifecycle.*
import com.vedant.futsnap.Network.ApiClient
import com.vedant.futsnap.Network.ResponseModels.PlayerTrackingRequest
import com.vedant.futsnap.UI.ErrorHandling.Error
import com.vedant.futsnap.Utils.NetworkResponse
import retrofit2.HttpException
import java.lang.Exception

class MainActivityViewModel(var apiClient: ApiClient): ViewModel() {

    var clientId: Int = 0

    var allPlayerTrackingRequests = MutableLiveData<List<PlayerTrackingRequest>>()


    fun getPlayerTrackingRequests():LiveData<NetworkResponse<List<PlayerTrackingRequest>>>{
        return liveData(context = viewModelScope.coroutineContext){
            try{
                val requests = apiClient.getPlayerTrackingRequests(clientId)
               emit(NetworkResponse.Success(requests))
            }
            catch (e: Exception){
                emit(NetworkResponse.Failure(Error.ServerError()))
            }
        }
    }

    fun addPlayerTrackingRequest(data: PlayerTrackingRequest):LiveData<NetworkResponse<PlayerTrackingRequest>>{
        return liveData(context = viewModelScope.coroutineContext){
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
                emit(NetworkResponse.Success(data))
            }
            catch (e:Exception){
                when(e){
                    is HttpException -> {
                        when(e.code()){
                            402 -> emit(NetworkResponse.Failure(
                                Error.GeneralError("Maximum " +
                                    "number of requests reached! Upgrade to premium to track " +
                                    "more players")))
                            409 -> emit(NetworkResponse.Failure(
                                Error.GeneralError("Cannot track a " +
                                    "duplicate player!")))
                            else -> emit(NetworkResponse.Failure(
                                Error.GeneralError("Request " +
                                    "Failed. Please try again!")))
                        }

                    }
                    else -> {
                        emit(NetworkResponse.Failure(
                            Error.ServerError("Could not connect to server. Player not added." +
                                " Please try again later!")))
                    }
                }

            }

        }
    }

    fun editPlayerTrackingRequest(playerId:Int, data: PlayerTrackingRequest):LiveData<NetworkResponse<PlayerTrackingRequest>>{
        return liveData(context = viewModelScope.coroutineContext){
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
                emit(NetworkResponse.Success(data))
            }
            catch (e: Exception){
                when(e){
                    is HttpException ->{
                        if(e.code() == 404){
                            emit(NetworkResponse.Failure(
                                Error.GeneralError("Player not found. " +
                                    "Please restart the application!")))
                        }
                    }
                    else -> {
                        emit(NetworkResponse.Failure(
                            Error.ServerError("Could not connect to server. Player not edited." +
                                " Please try again later!")))
                    }
                }
            }
        }

    }

    fun deletePlayerTrackingRequest(playerId: Int):LiveData<NetworkResponse<Int>>{
        return liveData(context = viewModelScope.coroutineContext){
            try {
                val deletedRequest = apiClient.deletePlayerTrackingRequests(playerId,clientId)
                val updatedList = allPlayerTrackingRequests.value?.filter { request ->
                    request.PlayerId != deletedRequest.PlayerId
                }
                allPlayerTrackingRequests.value = updatedList!!
                emit(NetworkResponse.Success(playerId))
            }
            catch (e:Exception){
                emit(NetworkResponse.Failure(Error.ServerError()))
            }
        }
    }

}