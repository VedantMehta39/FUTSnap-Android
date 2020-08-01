package com.vedant.futsnap

import android.app.Application
import com.vedant.futsnap.DI.ApplicationComponent
import com.vedant.futsnap.DI.DaggerApplicationComponent

class FUTBINWatcherApp:Application() {
    companion object{
        lateinit var component:ApplicationComponent
        private set
        const val maxNofOfTrackingRequests = 5

    }
    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        component = DaggerApplicationComponent.builder().build()
    }
}