package com.conspect.geogrocery.di

import com.conspect.geogrocery.data.repository.GroceryRepositoryImpl
import com.conspect.geogrocery.data.repository.LocationSearchRepositoryImpl
import com.conspect.geogrocery.domain.repository.GroceryRepository
import com.conspect.geogrocery.domain.repository.LocationSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGroceryRepository(impl: GroceryRepositoryImpl): GroceryRepository

    @Binds
    @Singleton
    abstract fun bindLocationSearchRepository(
        impl: LocationSearchRepositoryImpl
    ): LocationSearchRepository
}
