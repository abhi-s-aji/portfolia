package com.example.portfolia.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = 1L,
    val fullName: String = "",
    val headline: String = "",
    val bio: String = "",
    val email: String = "",
    val githubHandle: String = "",
    val linkedinHandle: String = "",
    val avatarUri: String? = null,
    val resumePdfUri: String? = null
)
