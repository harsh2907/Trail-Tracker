package com.example.trailtracker.mainScreen.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.trailtracker.mainScreen.data.FirebaseRunRepository
import com.example.trailtracker.mainScreen.domain.repositories.RunSessionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class UploadSessionsToFirebaseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val runSessionRepository: RunSessionRepository,
    private val firebaseRunRepository: FirebaseRunRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val unsyncedRuns = runSessionRepository.getUnsyncedRuns()

        unsyncedRuns.forEach { runEntity ->
            try {
                firebaseRunRepository.uploadRunSession(runEntity)
                    .onSuccess {
                        runSessionRepository.upsertRun(runEntity.copy(isSynced = true))
                        Log.d(
                            TAG,
                            "RunId:${runEntity.id} has been uploaded to firebase successfully"
                        )
                        //Clear room database for to save memory
                        runSessionRepository.deleteAllRuns()
                    }
                    .onFailure {
                        it.printStackTrace()
                        return Result.retry()
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                return Result.retry()
            }
        }

        return Result.success(
            workDataOf(
                KEY_RESULT to UUID.randomUUID().toString()
            )
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted context: Context,
            @Assisted workerParams: WorkerParameters
        ): UploadSessionsToFirebaseWorker
    }

    companion object {
        const val TAG = "UploadSessionsToFirebaseWorker"
        const val KEY_RESULT = "Result_Random_String"
    }

}

