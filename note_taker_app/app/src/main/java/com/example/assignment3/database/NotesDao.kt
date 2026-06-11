package com.example.assignment3.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignment3.models.Notes

@Dao
interface MainDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Notes)

    @Delete
    suspend fun delete(note: Notes)

    @Query("UPDATE notes SET title = :title, notes = :note WHERE id = :id")
    suspend fun update(id: Int, title: String, note: String)

    @Query("UPDATE notes SET pin = :pin WHERE id = :id")
    suspend fun pin(id: Int, pin: Boolean)

    // Pinned first, then newest
    @Query("SELECT * FROM notes ORDER BY pin DESC, id DESC")
    suspend fun getAll(): List<Notes>
}
