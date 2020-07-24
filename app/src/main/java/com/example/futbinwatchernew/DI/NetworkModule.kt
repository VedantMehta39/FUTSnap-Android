package com.example.futbinwatchernew.DI

import com.example.futbinwatchernew.Network.ApiClient
import com.example.futbinwatchernew.Network.FUTBINPriceDeserialiser
import com.example.futbinwatchernew.Network.ResponseModels.PlayerPriceWrapperResponse
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Named

const val SEARCH_BASE_URL = "https://www.futbin.com"
const val PRICE_BASE_URL = "https://futbin.org/futbin/api/fetchPlayerInformation/"
const val SERVICE_BASE_URL = "http://futsnap.prescient.co.in/api/"

@Module
class NetworkModule() {

    @Provides
    @Named("SEARCH")
    fun provideRetrofitForSearch():Retrofit {
        return Retrofit.Builder()
             .baseUrl(SEARCH_BASE_URL)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
    }

    @Provides
    @Named("PRICE")
    fun provideRetrofitForPrice():Retrofit{
        val responseType: Type = object: TypeToken<PlayerPriceWrapperResponse>() {}.type
        return Retrofit.Builder()
            .baseUrl(PRICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().
                registerTypeAdapter(responseType, FUTBINPriceDeserialiser()).create()))
            .build()
    }

    @Provides
    @Named("SERVICE")
    fun provideRetrofitForService():Retrofit{
        return Retrofit.Builder()
            .baseUrl(SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Named("SEARCH")
    fun provideApiClientForSearch(@Named("SEARCH") retrofit:Retrofit) = retrofit.create(ApiClient::class.java)

    @Provides
    @Named("PRICE")
    fun provideApiClientForPrice(@Named("PRICE") retrofit:Retrofit) = retrofit.create(ApiClient::class.java)

    @Provides
    @Named("SERVICE")
    fun provideApiClientForService(@Named("SERVICE") retrofit:Retrofit) = retrofit.create(ApiClient::class.java)


}