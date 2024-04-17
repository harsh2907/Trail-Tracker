package com.example.trailtracker.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DataStoreUtils(private val context: Context) {

    private val Context.datastore by preferencesDataStore(PREF_NAME)

    suspend fun setDetails(name:String,weight:Int) {
        context.datastore.edit { preferences ->
            preferences[NAME_PREF] = name
            preferences[WEIGHT_PREF] = weight
        }
    }

    fun getDetails():Flow<Pair<String,Int>>{
        return combine(getName(),getWeight()){name,weight->
            Pair(name,weight)
        }
    }

    private fun getName(): Flow<String> {
        return context.datastore.data.map { preferences ->
            preferences[NAME_PREF] ?: ""
        }
    }

    private fun getWeight(): Flow<Int> {
        return context.datastore.data.map { preferences ->
            preferences[WEIGHT_PREF] ?: 0
        }
    }

    companion object{
        private const val PREF_NAME = "TrackerPref"
        private val NAME_PREF = stringPreferencesKey("name")
        private val WEIGHT_PREF = intPreferencesKey("weight")
    }
}