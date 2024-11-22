package com.sample.sampleapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "tb_logs")
data class Log(@ColumnInfo("buttonName") val buttonName: String,
               @ColumnInfo("timestamp") val timestamp: Long = Calendar.getInstance().timeInMillis,
               @PrimaryKey(autoGenerate = true) var id: Int? = null)