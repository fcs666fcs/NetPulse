
package com.example.netpulse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NetPulseDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
