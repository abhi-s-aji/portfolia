package com.example.portfolia.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val category: String = "Android",
    val coverImageUri: String? = null,
    val tags: List<String> = emptyList(),
    val githubUrl: String? = null,
    val liveDemoUrl: String? = null,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
