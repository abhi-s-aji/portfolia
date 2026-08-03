package com.example.portfolia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reference_links")
data class ReferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val category: String = "General", // e.g., Docs, GitHub, Design, Tools
    val groupName: String = "General",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
