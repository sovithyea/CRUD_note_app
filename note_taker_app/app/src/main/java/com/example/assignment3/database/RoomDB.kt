package com.example.assignment3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.assignment3.models.Notes

@Database(entities = [Notes::class], version = 4, exportSchema = false)
abstract class RoomDB : RoomDatabase() {
    abstract fun mainDAO(): MainDAO

    companion object {
        @Volatile private var INSTANCE: RoomDB? = null
        private const val DATABASE_NAME = "NoteKeeper"

        fun getInstance(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    DATABASE_NAME
                )
                    .build().also { INSTANCE = it }
            }
        }
    }
}
