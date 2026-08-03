package com.example.portfolia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val demoUrl: String = "",
    val githubUrl: String = "",
    val linkedinPostUrl: String = "",
    val techStack: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)
