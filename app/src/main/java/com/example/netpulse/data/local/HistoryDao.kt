
package com.example.netpulse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM test_history ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM test_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): HistoryEntity?

    @Insert
    suspend fun insert(entity: HistoryEntity): Long

    @Delete
    suspend fun delete(entity: HistoryEntity)

    @Query("DELETE FROM test_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
