package com.aichat.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert
    suspend fun insert(record: ChatRecord)

    @Query("SELECT * FROM chat_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ChatRecord>>
}
