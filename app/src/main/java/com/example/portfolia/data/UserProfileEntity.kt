package com.example.portfolia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Single profile row
    val name: String = "Your Name",
    val title: String = "Software Developer",
    val bio: String = "Passionate developer crafting high quality applications.",
    val email: String = "developer@example.com",
    val githubUrl: String = "https://github.com",
    val linkedinUrl: String = "https://linkedin.com"
)
