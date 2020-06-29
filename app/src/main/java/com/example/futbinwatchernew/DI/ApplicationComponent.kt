package com.example.futbinwatchernew.DI

import com.example.futbinwatchernew.SearchPlayerViewModel
import dagger.Component

@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface ApplicationComponent {
    fun inject(vmodel:SearchPlayerViewModel)
}