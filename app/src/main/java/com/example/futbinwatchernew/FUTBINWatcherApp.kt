package com.example.futbinwatchernew

import android.app.Application
import com.example.futbinwatchernew.DI.ApplicationComponent
import com.example.futbinwatchernew.DI.DaggerApplicationComponent
import com.example.futbinwatchernew.DI.NetworkModule

class FUTBINWatcherApp:Application() {
    companion object{
        const val SEARCH_BASE_URL = "https://www.futbin.com"
        const val PRICE_BASE_URL = "https://futbin.org/futbin/api/fetchPlayerInformation/"
        const val SERVICE_BASE_URL = "http://localhost:4000/api/"
        var component = HashMap<String,ApplicationComponent>()
        private set
        lateinit var instance:FUTBINWatcherApp
        private set

    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        initDagger()
    }

    fun initDagger() {
        component["SEARCH"] =
            DaggerApplicationComponent.builder()
                .networkModule(NetworkModule(SEARCH_BASE_URL)).build()
        component["PRICE"] =
            DaggerApplicationComponent.builder()
                .networkModule(NetworkModule(PRICE_BASE_URL)).build()
        component["SERVICE"] =
            DaggerApplicationComponent.builder()
                .networkModule(NetworkModule(SERVICE_BASE_URL)).build()

    }
}