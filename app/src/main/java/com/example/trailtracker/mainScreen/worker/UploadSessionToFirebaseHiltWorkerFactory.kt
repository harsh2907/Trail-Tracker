package com.example.trailtracker.mainScreen.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject

class UploadSessionToFirebaseHiltWorkerFactory @Inject constructor(
    private val uploadWorkerFactory: UploadSessionsToFirebaseWorker.Factory
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            UploadSessionsToFirebaseWorker::class.java.name -> 
                uploadWorkerFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}
