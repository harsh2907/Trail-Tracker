package com.example.trailtracker.mainScreen.presentation.screens.home.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trailtracker.mainScreen.domain.models.Run


@Database(entities = [Run::class], version = 1, exportSchema = false)
@TypeConverters(BitmapConverter::class)
abstract class RunDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao
}
