package com.example.trailtracker.mainScreen.services

import android.content.Context
import com.example.trailtracker.mainScreen.presentation.screens.home.data.local.RunDao

class UploadRunWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val runDao: RunDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val unsyncedRuns = runDao.getUnsyncedRuns() // Get runs not yet synced

        unsyncedRuns.forEach { runEntity ->
            try {
                // Upload the image to Firebase Storage
                val imageUrl = uploadBitmapToFirebase(runEntity)

                // Convert RunEntity to Firebase Run model
                val run = runEntity.toRun().copy(
                    imageUrl = imageUrl
                )
                val run = Run(
                    id = runEntity.id,
                    imageUrl = imageUrl,
                    createdAt = runEntity.createdAt,
                    sessionDuration = runEntity.sessionDuration,
                    averageSpeedInKPH = runEntity.averageSpeedInKPH,
                    distanceCovered = runEntity.distanceCovered,
                    caloriesBurned = runEntity.caloriesBurned
                )

                // Upload run data to Firebase Firestore or Realtime Database
                uploadRunDataToFirebase(run)

                // Mark the run as synced in Room
                runDao.updateRun(runEntity.copy(isSynced = true))

            } catch (e: Exception) {
                // Retry the upload if there’s a failure
                return Result.retry()
            }
        }
        return Result.success()
    }

    private suspend fun uploadBitmapToFirebase(runEntity: RunEntity): String {
        // Upload bitmap and return the URL (as discussed earlier)
    }

    private suspend fun uploadRunDataToFirebase(run: Run) {
        // Upload run data to Firestore or Firebase Realtime Database
    }
}
