package com.jayvijay.gitpeek.domain.repository

import androidx.paging.PagingData
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(username: String): Result<User>

    suspend fun getRepository(
        username: String,
        repositoryName: String,
    ): Result<Repository>

    suspend fun getUserForkCount(
        username: String,
        totalRepositoryCount: Int,
    ): Result<Int>

    fun getUserRepositoriesPaging(username: String): Flow<PagingData<Repository>>
}
