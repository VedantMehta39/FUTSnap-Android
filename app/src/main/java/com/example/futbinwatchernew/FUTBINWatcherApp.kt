package com.example.futbinwatchernew

import android.app.Application
import com.example.futbinwatchernew.DI.ApplicationComponent
import com.example.futbinwatchernew.DI.DaggerApplicationComponent
import com.example.futbinwatchernew.DI.NetworkModule

class FUTBINWatcherApp:Application() {
    companion object{
        lateinit var component:ApplicationComponent
        private set
        val maxNofOfTrackingRequests = 5

    }
    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        component = DaggerApplicationComponent.builder().build()
    }
}