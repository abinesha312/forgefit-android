package com.forgefit.android.data.repository

import com.forgefit.android.data.db.dao.UserProfileDao
import com.forgefit.android.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userProfileDao: UserProfileDao) {
    
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()
    
    suspend fun getUserProfileOnce(): UserProfile? = userProfileDao.getUserProfileOnce()
    
    suspend fun saveUserProfile(profile: UserProfile) = userProfileDao.insertOrUpdate(profile)
    
    suspend fun updateUserProfile(profile: UserProfile) = userProfileDao.update(profile)
    
    suspend fun isOnboardingCompleted(): Boolean = userProfileDao.isOnboardingCompleted() ?: false
}
