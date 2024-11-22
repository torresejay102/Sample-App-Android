package com.sample.sampleapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query

@Dao
interface LogDao {
    @Insert(onConflict = IGNORE)
    fun insertLog(vararg logs: Log)

    @Query("SELECT * FROM tb_logs ORDER BY buttonName")
    fun getAllLogsByButtonName(): List<Log?>?

    @Query("SELECT * FROM tb_logs ORDER BY timestamp DESC")
    fun getAllLogsByDate(): List<Log?>?
}
