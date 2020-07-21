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


    fun addOrUpdateTokenOnServer(context: Context, newToken: String): LiveData<NetworkResponse<Int>> {
        val sharedPrefRepo = SharedPrefRepo(context, SharedPrefFileNames.CLIENT_REGISTRATION)
        val clientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
        return if (clientId == -1) {
            addClient(Client(0, newToken, null))
        } else {
            editClient(Client(clientId, newToken, null))
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