package com.example.futbinwatchernew

import android.app.Application
import com.example.futbinwatchernew.DI.ApplicationComponent
import com.example.futbinwatchernew.DI.DaggerApplicationComponent
import com.example.futbinwatchernew.DI.DatabaseModule
import com.example.futbinwatchernew.DI.NetworkModule

class FUTBINWatcherApp:Application() {
    companion object{
        const val SEARCH_BASE_URL = "https://www.futbin.com"
        const val PRICE_BASE_URL = "https://futbin.org/futbin/api/fetchPlayerInformation/"
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
        component.put("SEARCH", DaggerApplicationComponent.builder().
            databaseModule(DatabaseModule(this)).networkModule(NetworkModule(SEARCH_BASE_URL)).build())
        component.put("PRICE", DaggerApplicationComponent.builder().
            databaseModule(DatabaseModule(this)).networkModule(NetworkModule(PRICE_BASE_URL)).build())
    }
}