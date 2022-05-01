package com.geekydroid.sellquick.di

import com.geekydroid.sellquickbackend.data.datasource.LocalDataSource
import com.geekydroid.sellquickbackend.repository.ItemRepository
import com.geekydroid.sellquickbackend.repository.OrderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesItemRepository(database: LocalDataSource): ItemRepository {
        return ItemRepository(database)
    }

    @Provides
    @Singleton
    fun providesOrderRepository(database: LocalDataSource): OrderRepository {
        return OrderRepository(database)
    }

}