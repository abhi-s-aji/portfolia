package com.example.portfolia.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {
    @Query("SELECT * FROM code_snippets ORDER BY id DESC")
    fun getAllSnippets(): Flow<List<SnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: SnippetEntity)

    @Delete
    suspend fun deleteSnippet(snippet: SnippetEntity)
}
