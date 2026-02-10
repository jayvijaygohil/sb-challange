package com.jayvijay.gitpeek.domain.usecase

import com.jayvijay.gitpeek.domain.model.User
import com.jayvijay.gitpeek.domain.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
class GetUserUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(username: String): Result<User> = repository.getUser(username)
}
