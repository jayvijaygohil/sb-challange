package com.jayvijay.gitpeek.domain.usecase

import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
class GetRepositoryUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(
        username: String,
        repositoryName: String,
    ): Result<Repository> = repository.getRepository(username, repositoryName)
}
