package com.example.portfolia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val techStack: String,
    val githubUrl: String = "",
    val demoUrl: String = "",
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
