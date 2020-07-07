package com.example.futbinwatchernew.DI

import com.example.futbinwatchernew.SearchPlayerViewModel
import com.example.futbinwatchernew.Services.RegisterClientService
import com.example.futbinwatchernew.Services.UploadTrackedPlayersService
import dagger.Component

@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface ApplicationComponent {
    fun inject(vmodel:SearchPlayerViewModel)
    fun inject(service: UploadTrackedPlayersService)
    fun inject(service:RegisterClientService)
}