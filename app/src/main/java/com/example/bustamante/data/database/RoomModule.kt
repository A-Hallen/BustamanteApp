package com.example.bustamante.data.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val DATABASE_NAME = "bustamantedb"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, Database::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideProductosDao(database: Database) = database.getProductosDao()

    @Singleton
    @Provides
    fun provideProveedoresDao(database: Database) = database.getProveedoresDao()

    @Singleton
    @Provides
    fun provideDateDao(database: Database) = database.getDateDao()
}