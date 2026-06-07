package com.example.data.local

import androidx.room.*
import com.example.data.model.Saving
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingDao {
    @Query("SELECT * FROM savings ORDER BY date DESC, id DESC")
    fun getAllSavings(): Flow<List<Saving>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaving(saving: Saving)

    @Update
    suspend fun updateSaving(saving: Saving)

    @Delete
    suspend fun deleteSaving(saving: Saving)

    @Query("DELETE FROM savings WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM savings")
    suspend fun deleteAllSavings()
}
