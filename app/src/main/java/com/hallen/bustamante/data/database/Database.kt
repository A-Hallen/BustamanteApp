package com.hallen.bustamante.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hallen.bustamante.data.model.DateDao
import com.hallen.bustamante.data.model.DateEntity
import com.hallen.bustamante.data.model.ProductosDao
import com.hallen.bustamante.data.model.ProductosEntity
import com.hallen.bustamante.data.model.ProveedorDao
import com.hallen.bustamante.data.model.ProveedorEntity
import java.util.Date

@Database(
    entities = [ProductosEntity::class, ProveedorEntity::class, DateEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun getProductosDao(): ProductosDao
    abstract fun getProveedoresDao(): ProveedorDao
    abstract fun getDateDao(): DateDao

}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
