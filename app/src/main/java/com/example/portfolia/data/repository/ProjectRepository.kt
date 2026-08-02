package com.example.portfolia.data.repository

import com.example.portfolia.data.local.ProjectDao
import com.example.portfolia.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getFeaturedProjects(): Flow<List<ProjectEntity>> = projectDao.getFeaturedProjects()

    fun getProjectById(id: Long): Flow<ProjectEntity?> = projectDao.getProjectById(id)

    fun searchProjects(query: String, category: String): Flow<List<ProjectEntity>> =
        projectDao.searchProjects(query, category)

    suspend fun insertProject(project: ProjectEntity): Long =
        projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) =
        projectDao.updateProject(project)

    suspend fun deleteProject(project: ProjectEntity) =
        projectDao.deleteProject(project)

    suspend fun deleteProjectById(id: Long) =
        projectDao.deleteProjectById(id)

    suspend fun deleteAllProjects() =
        projectDao.deleteAllProjects()

    suspend fun getAllProjectsSnapshot(): List<ProjectEntity> =
        projectDao.getAllProjectsSnapshot()
}
