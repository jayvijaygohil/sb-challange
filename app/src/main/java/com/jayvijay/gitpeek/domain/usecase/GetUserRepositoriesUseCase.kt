package com.jayvijay.gitpeek.domain.usecase

import androidx.paging.PagingData
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetUserRepositoriesUseCase(
    private val repository: UserRepository,
) {
    operator fun invoke(username: String): Flow<PagingData<Repository>> = repository.getUserRepositoriesPaging(username)
}
