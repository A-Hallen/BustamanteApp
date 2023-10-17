package com.hallen.bustamante.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.Date

@Dao
interface DateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dateEntity: DateEntity)

    @Query("SELECT date FROM updates_table WHERE id=:id")
    suspend fun getLastUpdateDate(id: String): Date?
}
