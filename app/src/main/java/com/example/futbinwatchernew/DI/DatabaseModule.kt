package com.example.futbinwatchernew.DI

import androidx.room.Room
import com.example.futbinwatchernew.Database.FUTBINWatcherDatabase
import com.example.futbinwatchernew.Database.PlayerDAO
import com.example.futbinwatchernew.FUTBINWatcherApp
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule(var application:FUTBINWatcherApp) {
    @Provides
    fun provideDatabase():FUTBINWatcherDatabase{
        return Room.databaseBuilder(application, FUTBINWatcherDatabase::class.java,
            "FUTBINWatcher_database").build()
    }

    @Provides
    fun providePlayerDao(db:FUTBINWatcherDatabase):PlayerDAO{
        return db.getPlayerDao()
    }
}