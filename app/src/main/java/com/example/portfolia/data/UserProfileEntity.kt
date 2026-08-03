package com.example.portfolia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Single profile row
    val name: String = "",
    val title: String = "",
    val bio: String = "",
    val email: String = "",
    val githubUrl: String = "",
    val linkedinUrl: String = "",
    val experienceYears: Int = 0,
    val uptime: String = "99.9%",
    val commits: String = "12.4k",
    val avatarUri: String? = null
)
