package com.vedant.futsnap.DI

import com.vedant.futsnap.Network.ApiClient
import com.vedant.futsnap.Network.PriceDeserialiser
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.vedant.futsnap.UI.Models.Platform
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*
import javax.inject.Named

const val FUTBIN_BASE_URL = "https://www.futbin.com"
const val SERVICE_BASE_URL = "https://futsnap.prescient.co.in/api/"

@Module
class NetworkModule {

    @Provides
    @Named("SEARCH")
    fun provideRetrofitForSearch():Retrofit {
        return Retrofit.Builder()
             .baseUrl(FUTBIN_BASE_URL)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
    }

    @Provides
    @Named("PRICE")
    fun provideRetrofitForPrice():Retrofit{
        val responseType: Type = object: TypeToken<EnumMap<Platform, Int?>>() {}.type
        return Retrofit.Builder()
            .baseUrl(FUTBIN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().
                registerTypeAdapter(responseType, PriceDeserialiser()).create()))
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
    fun provideApiClientForSearch(@Named("SEARCH") retrofit:Retrofit): ApiClient = retrofit.create(ApiClient::class.java)

    @Provides
    @Named("PRICE")
    fun provideApiClientForPrice(@Named("PRICE") retrofit:Retrofit): ApiClient = retrofit.create(ApiClient::class.java)

    @Provides
    @Named("SERVICE")
    fun provideApiClientForService(@Named("SERVICE") retrofit:Retrofit): ApiClient = retrofit.create(ApiClient::class.java)


}