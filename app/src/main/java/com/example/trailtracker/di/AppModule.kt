package com.example.trailtracker.di

import android.content.Context
import com.example.trailtracker.datastore.DataStoreUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatastoreUtils(@ApplicationContext context:Context) = DataStoreUtils(context)
}