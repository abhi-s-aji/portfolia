package com.example.portfolia.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.portfolia.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isFeatured = 1 ORDER BY createdAt DESC")
    fun getFeaturedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: Long): Flow<ProjectEntity?>

    @Query(
        """
        SELECT * FROM projects
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        AND (:category = 'All' OR category = :category)
        ORDER BY createdAt DESC
        """
    )
    fun searchProjects(query: String, category: String): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long)

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    suspend fun getAllProjectsSnapshot(): List<ProjectEntity>
}
