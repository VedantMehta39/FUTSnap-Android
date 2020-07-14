package com.example.futbinwatchernew.DI

import androidx.lifecycle.ViewModel
import com.example.futbinwatchernew.MainActivityViewModel
import com.example.futbinwatchernew.SearchPlayerViewModel
import com.example.futbinwatchernew.Services.RegisterClientService
import com.example.futbinwatchernew.SinglePlayerDialogFragmentViewModel
import dagger.Component

@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun inject(vmodel:MainActivityViewModel)
    fun inject(vmodel:SearchPlayerViewModel)
    fun inject(vmodel:SinglePlayerDialogFragmentViewModel)
    fun inject(service:RegisterClientService)
}