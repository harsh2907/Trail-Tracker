package com.example.trailtracker.mainScreen.data

import android.graphics.Bitmap
import com.example.trailtracker.mainScreen.domain.models.Run
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream

class FirebaseRunRepository {

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    private val currentUser get() = Firebase.auth.currentUser
    private val userId
        get() = currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private val runsCollection = firestore.collection("users").document(userId).collection("runs")

    private val _runsFlow = MutableStateFlow<List<Run>>(emptyList())
    val runsFlow: StateFlow<List<Run>> = _runsFlow

    private var listenerRegistration: ListenerRegistration? = null

    init {
        listenerRegistration = runsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error if needed
                return@addSnapshotListener
            }
            snapshot?.let {
                val runs = it.toObjects(Run::class.java)
                _runsFlow.value = runs
            }
        }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }


    suspend fun saveRunSession(run: Run, image: Bitmap) = runCatching {
        val documentRef = runsCollection.document(run.id)

        val imageUrl = uploadImageToStorage(run.id, image)
        val runToUpload = run.copy(imageUrl = imageUrl)

        documentRef.set(runToUpload).await()
    }

    suspend fun updateRun(run: Run) = runCatching {
        runsCollection.document(run.id).set(run).await()
    }

    suspend fun deleteRun(run: Run): Result<Unit> = runCatching {
        runsCollection.document(run.id).delete().await()
        deleteImageFromStorage(run.id)
    }

    suspend fun deleteAllRuns(): Result<Unit> = runCatching {
        val snapshot = runsCollection.get().await()
        val batch = firestore.batch()

        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
            deleteImageFromStorage(runId = document.id)
        }

        batch.commit().await()
    }

    suspend fun getRunById(id: String): Result<Run?> = runCatching {
        runsCollection.document(id).get().await().toObject(Run::class.java)
    }

    fun getAllRunsSortedByDate(): Flow<Result<List<Run>>> {
        return getRunsSortedBy("createdAt")
    }

    fun getAllRunsSortedBySpeed(): Flow<Result<List<Run>>> {
        return getRunsSortedBy("averageSpeedInKPH")
    }

    fun getAllRunsSortedByDistance(): Flow<Result<List<Run>>> {
        return getRunsSortedBy("distanceCovered")
    }

    fun getAllRunsSortedByDuration(): Flow<Result<List<Run>>> {
        return getRunsSortedBy("sessionDuration")
    }

    fun getAllRunsSortedByCalories(): Flow<Result<List<Run>>> {
        return getRunsSortedBy("caloriesBurned")
    }

    fun getTotalDurationForSessions(): Flow<Long> {
        return runsFlow.map {run-> run.sumOf { it.sessionDuration } }
    }


    fun getTotalCaloriesBurnedForSessions(): Flow<Int> {
        return runsFlow.map{run-> run.sumOf { it.caloriesBurned } }
    }

    fun getTotalDistanceCoveredForSessions(): Flow<Double> {
        return runsFlow.map {run-> run.sumOf { it.distanceCovered } }

    }

    fun getTotalAverageSpeedForSessions(): Flow<Double> {
        return runsFlow.map { runs ->
            val totalDistance = runs.sumOf { it.distanceCovered }
            val totalTime = runs.sumOf { it.sessionDuration }
            if (totalTime > 0) (totalDistance / totalTime) * 3600 else 0.0
        }
    }

    private fun getRunsSortedBy(field: String): Flow<Result<List<Run>>> = flow {
        val snapshot = runsCollection.orderBy(field, Query.Direction.DESCENDING).get().await()
        val runs = snapshot.toObjects(Run::class.java)
        emit(Result.success(runs))
    }.catch { exception ->
        emit(Result.failure(exception))
    }



    private suspend fun uploadImageToStorage(
        runId: String,
        bitmap: Bitmap
    ): String? {
        return runCatching {
            val imageRef = storage.reference.child("$userId/$runId.jpg")
            val byteArray = ByteArrayOutputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.toByteArray()
            }

            val uploadTask = imageRef.putBytes(byteArray).await()
            uploadTask.storage.downloadUrl.await().toString()
        }.getOrNull()
    }

    private suspend fun deleteImageFromStorage(runId: String): Result<Unit> {
        return runCatching {
            val imageRef = storage.reference.child("$userId/$runId.jpg")
            imageRef.delete().await()
        }
    }
}
