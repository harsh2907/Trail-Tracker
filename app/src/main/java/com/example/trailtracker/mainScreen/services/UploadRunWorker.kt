package com.example.trailtracker.mainScreen.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.trailtracker.mainScreen.domain.models.RunEntity
import com.example.trailtracker.mainScreen.presentation.screens.home.data.local.RunDao

class UploadRunWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val runDao: RunDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
/*        val unsyncedRuns = runDao.getUnsyncedRuns() // Get runs not yet synced

        unsyncedRuns.forEach { runEntity ->
            try {
                // Upload the image to Firebase Storage
                val imageUrl = uploadBitmapToFirebase(runEntity)

                // Convert RunEntity to Firebase Run model
                val run = runEntity.toRun().copy(
                    imageUrl = imageUrl
                )

                // Upload run data to Firebase Firestore or Realtime Database
                uploadRunDataToFirebase(run)

                // Mark the run as synced in Room
                runDao.upsertRun(runEntity.copy(isSynced = true))

            } catch (e: Exception) {
                // Retry the upload if there’s a failure
                return Result.retry()
            }
        }*/
        return Result.success()
    }


}
