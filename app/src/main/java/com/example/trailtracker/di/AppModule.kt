package com.example.trailtracker.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.trailtracker.mainScreen.presentation.screens.home.data.local.RunDao
import com.example.trailtracker.mainScreen.presentation.screens.home.data.local.RunDatabase
import com.example.trailtracker.datastore.DataStoreUtils
import com.example.trailtracker.mainScreen.data.FirebaseRunRepository
import com.example.trailtracker.mainScreen.domain.repositories.FirebaseUserRepository
import com.example.trailtracker.mainScreen.domain.repositories.RunSessionRepository
import com.example.trailtracker.mainScreen.domain.usecases.SortRunsUseCase
import com.example.trailtracker.mainScreen.presentation.screens.home.data.repository.RunSessionOfflineRepositoryImpl
import com.example.trailtracker.mainScreen.worker.UploadSessionsToFirebaseWorker
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
    fun provideRunDatabase(@ApplicationContext context: Context): RunDatabase {
        return Room.databaseBuilder(
            context,
            RunDatabase::class.java,
            "run_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideRunDao(
        runDatabase: RunDatabase
    ): RunDao = runDatabase.runDao()

    @Provides
    @Singleton
    fun provideRunRepository(
        runDao: RunDao
    ): RunSessionRepository = RunSessionOfflineRepositoryImpl(runDao)

    @Provides
    @Singleton
    fun provideFirebaseUserRepository() = FirebaseUserRepository()

    @Provides
    @Singleton
    fun provideFirebaseRunRepository() = FirebaseRunRepository()


    @Provides
    @Singleton
    fun provideSortRunsUseCase(
        firebaseRunRepository: FirebaseRunRepository
    ): SortRunsUseCase = SortRunsUseCase(firebaseRunRepository)


}