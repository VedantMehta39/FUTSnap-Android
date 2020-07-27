package com.example.futbinwatchernew.Utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.ResponseModels.Client
import com.example.futbinwatchernew.UI.ErrorHandling.Error
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class ClientUtility @Inject constructor( @Named("SERVICE") var apiClient: ApiClient) {


    fun addOrUpdateTokenOnServer(client: Client): LiveData<NetworkResponse<Int>> {

        return if (client.Id == 0) {
            addClient(client)
        } else {
            editClient(client)
        }

    }

    private fun addClient(client: Client): LiveData<NetworkResponse<Int>> {
        client.MaxNumberOfRequests = FUTBINWatcherApp.maxNofOfTrackingRequests
        return liveData {
            try {
                val newClientId = apiClient.postClient(client)
                emit(NetworkResponse.Success(newClientId))
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(Error.RegistrationError()))
            }
        }
    }

    private fun editClient(client: Client): LiveData<NetworkResponse<Int>> {
        client.MaxNumberOfRequests = FUTBINWatcherApp.maxNofOfTrackingRequests
        return liveData {
            try {
                val clientId = apiClient.putClient(client.Id, client)
                emit(NetworkResponse.Success(clientId))
            } catch (e: Exception) {
                emit(NetworkResponse.Failure(Error.RegistrationError()))
            }
        }

    }
}