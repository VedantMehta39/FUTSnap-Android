package com.example.futbinwatchernew.DI

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