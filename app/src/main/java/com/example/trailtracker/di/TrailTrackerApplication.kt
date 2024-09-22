package com.example.trailtracker.di

import android.app.Application
import androidx.work.Configuration
import com.example.trailtracker.mainScreen.worker.UploadSessionToFirebaseHiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TrailTrackerApplication:Application(),Configuration.Provider{

    @Inject
    lateinit var workerFactory: UploadSessionToFirebaseHiltWorkerFactory


    override val workManagerConfiguration: Configuration
        get() =  Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}