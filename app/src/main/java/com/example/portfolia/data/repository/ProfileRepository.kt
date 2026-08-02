package com.example.portfolia.data.repository

import com.example.portfolia.data.local.ProfileDao
import com.example.portfolia.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val profileDao: ProfileDao) {

    fun getProfile(): Flow<UserProfileEntity?> = profileDao.getProfile()

    suspend fun getProfileSnapshot(): UserProfileEntity? = profileDao.getProfileSnapshot()

    suspend fun saveProfile(profile: UserProfileEntity) =
        profileDao.insertOrUpdateProfile(profile)

    suspend fun deleteProfile() = profileDao.deleteProfile()
}
