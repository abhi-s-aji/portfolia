package com.example.portfolia.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SecretDao {
    @Query("SELECT * FROM dev_secrets ORDER BY id DESC")
    fun getAllSecrets(): Flow<List<DevSecretEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecret(secret: DevSecretEntity)

    @Delete
    suspend fun deleteSecret(secret: DevSecretEntity)
}
