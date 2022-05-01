package com.geekydroid.sellquick.di

import android.app.Application
import androidx.room.Room
import com.geekydroid.sellquickbackend.data.datasource.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providesLocalDataSource(app: Application): LocalDataSource {
        return Room.databaseBuilder(
            app.applicationContext,
            LocalDataSource::class.java,
            "sellquick.db"
        )
            .createFromAsset("database/sellquick.db")
            .build()
    }


}