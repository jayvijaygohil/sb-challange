package com.jayvijay.gitpeek.domain.usecase

import com.jayvijay.gitpeek.domain.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
class GetUserForkCountUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(
        username: String,
        totalRepositoryCount: Int,
    ): Result<Int> = repository.getUserForkCount(username, totalRepositoryCount)
}
