package com.example.futbinwatchernew.DI

import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.FUTBINPriceDeserialiser
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceResponse
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceWrapperResponse
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

@Module
class NetworkModule(var baseURL:String) {

    @Provides
    fun provideRetrofit():Retrofit {
       val retrofit:Retrofit
        if(baseURL.equals(FUTBINWatcherApp.PRICE_BASE_URL)){
            val responseType:Type = object: TypeToken<PlayerPriceWrapperResponse>() {}.type
            retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().
                    registerTypeAdapter(responseType,FUTBINPriceDeserialiser()).create()))
                .build()
        }
        else{
            retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

    @Provides
    fun provideApiClient(retrofit:Retrofit) = retrofit.create(ApiClient::class.java)


}