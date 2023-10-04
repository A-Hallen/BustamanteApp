package com.example.bustamante.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "updates_table")
data class DateEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") var id: String = "productos",
    @ColumnInfo(name = "date") var date: Date
)
