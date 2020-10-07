package com.vedant.futsnap.Utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.vedant.futsnap.FUTSnapApp
import com.vedant.futsnap.Network.ApiClient
import com.vedant.futsnap.Network.ResponseModels.Client
import com.vedant.futsnap.UI.ErrorHandling.Error
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class ClientUtility @Inject constructor( @Named("SERVICE") var apiClient: ApiClient) {
    suspend fun getClientByEmail(email:String):NetworkResponse<Client>{
        try{
            val client = apiClient.getClientByEmail(email)
            return NetworkResponse.Success(client)
        }
        catch (e:Exception){
            when(e){
                is HttpException ->{
                    if(e.code() == 404){
                        return NetworkResponse.Failure(Error.ServerError("User email not found!"))
                    }
                    else{
                        return NetworkResponse.Failure(Error.ServerError("Some error occurred. " +
                                "Please try again later!"))
                    }
                }
                else -> return NetworkResponse.Failure(Error.ServerError("Some error occurred. " +
                        "Please try again later!"))
            }
        }
    }

    fun addOrUpdateTokenOnServer(client: Client): LiveData<NetworkResponse<Int>> {

        return if (client.Id == 0) {
            addClient(client)
        } else {
            editClient(client)
        }

    }

    private fun addClient(client: Client): LiveData<NetworkResponse<Int>> {
        client.MaxNumberOfRequests = FUTSnapApp.maxNofOfTrackingRequests
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
        client.MaxNumberOfRequests = FUTSnapApp.maxNofOfTrackingRequests
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