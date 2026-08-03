package com.example.portfolia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "code_snippets")
data class SnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val language: String, // "KOTLIN", "BASH", "SQL", "DOCKER", "JSON", "YAML", "OTHER"
    val codeContent: String,
    val description: String? = null
)
