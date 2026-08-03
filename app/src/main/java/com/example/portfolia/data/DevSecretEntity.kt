package com.example.portfolia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dev_secrets")
data class DevSecretEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val secretType: String, // "API_KEY", "ENV_VAR", "WEB_LINK_CONFIG"
    val envNameOrUrl: String?,
    val secretValue: String, // Encrypted string value
    val linkedProjectId: Long? = null
)
