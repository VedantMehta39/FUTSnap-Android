package com.vedant.futsnap.DI

import com.vedant.futsnap.UI.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class,ViewModelModule::class])
interface ApplicationComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(fragment:SearchedPlayersFragment)
    fun inject(fragment: SinglePlayerDialog)
    fun inject(fragment: TrackedPlayersFragment)
    fun inject(fragment:ErrorFragment)
}