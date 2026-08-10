package com.conspect.geogrocery.di

import android.content.Context
import androidx.room.Room
import com.conspect.geogrocery.data.local.GeoGroceryDatabase
import com.conspect.geogrocery.data.local.GroceryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GeoGroceryDatabase =
        Room.databaseBuilder(context, GeoGroceryDatabase::class.java, GeoGroceryDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGroceryDao(db: GeoGroceryDatabase): GroceryDao = db.groceryDao()
}
