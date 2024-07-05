package com.example.trailtracker.di

import android.content.Context
import androidx.room.Room
import com.example.trailtracker.data.local.RunDatabase
import com.example.trailtracker.datastore.DataStoreUtils
import com.example.trailtracker.mainScreen.data.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    fun provideDatastoreUtils(@ApplicationContext context: Context) = DataStoreUtils(context)

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        fusedLocationProviderClient: FusedLocationProviderClient
    ): LocationRepository {
        return LocationRepository(fusedLocationProviderClient)
    }

    @Provides
    @Singleton
    fun provideRunDatabase(@ApplicationContext context: Context):RunDatabase{
        return Room.databaseBuilder(
            context,
            RunDatabase::class.java,
            "run_database"
        ).build()
    }
}