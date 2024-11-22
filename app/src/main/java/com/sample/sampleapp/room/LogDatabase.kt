package com.sample.sampleapp.room

import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.sample.sampleapp.SampleApp

@Database(entities = [Log::class], version = 1)
abstract class LogDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao?

    companion object {
        @get:Synchronized
        var instance: LogDatabase? = null
            get() {
                if (field == null) {
                    field = databaseBuilder(
                        SampleApp.get()!!,
                        LogDatabase::class.java, "Logs")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
                return field
            }
            private set
    }
}