package com.example.futbinwatchernew.DI

import androidx.lifecycle.ViewModel
import com.example.futbinwatchernew.MainActivityViewModel
import com.example.futbinwatchernew.SearchPlayerViewModel
import com.example.futbinwatchernew.Services.RegisterClientService
import com.example.futbinwatchernew.SinglePlayerDialogFragmentViewModel
import com.example.futbinwatchernew.UI.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class,ViewModelModule::class])
interface ApplicationComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment:SearchedPlayersFragment)
    fun inject(fragment: SinglePlayerDialog)
    fun inject(fragment: TrackedPlayersFragment)
    fun inject(fragment:ErrorFragment)
}