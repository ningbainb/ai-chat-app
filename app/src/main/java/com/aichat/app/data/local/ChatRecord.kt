package com.aichat.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_records")
data class ChatRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val goal: String,
    val style: String,
    val suggestionsJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
