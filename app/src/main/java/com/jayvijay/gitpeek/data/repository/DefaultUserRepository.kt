package com.jayvijay.gitpeek.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jayvijay.gitpeek.data.mapper.toGitPeekException
import com.jayvijay.gitpeek.data.network.GithubService
import com.jayvijay.gitpeek.data.network.response.toRepository
import com.jayvijay.gitpeek.data.network.response.toUser
import com.jayvijay.gitpeek.data.source.UserRepositoryPagingSource
import com.jayvijay.gitpeek.domain.model.GitPeekException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UserNotFoundException
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.model.User
import com.jayvijay.gitpeek.domain.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [UserRepository::class])
class DefaultUserRepository(
    private val githubService: GithubService,
) : UserRepository {
    companion object {
        private const val PAGE_SIZE = 30
        private const val TOTAL_USER_FORKS_PAGE_SIZE = 100
    }

    override suspend fun getUser(username: String): Result<User> =
        safeApiCall(
            httpMapping = { code ->
                if (code == 404) UserNotFoundException("User not found") else null
            },
        ) { githubService.getUser(username).toUser() }

    override suspend fun getRepository(
        username: String,
        repositoryName: String,
    ): Result<Repository> = safeApiCall { githubService.getRepository(username, repositoryName).toRepository() }

    override suspend fun getUserForkCount(
        username: String,
        totalRepositoryCount: Int,
    ): Result<Int> =
        safeApiCall {
            coroutineScope {
                if (totalRepositoryCount == 0) return@coroutineScope 0

                val totalPages =
                    (totalRepositoryCount + TOTAL_USER_FORKS_PAGE_SIZE - 1) / TOTAL_USER_FORKS_PAGE_SIZE

                (1..totalPages)
                    .map { page ->
                        async {
                            githubService.getUserRepos(
                                userName = username,
                                perPage = TOTAL_USER_FORKS_PAGE_SIZE,
                                page = page,
                            )
                        }
                    }.awaitAll()
                    .flatten()
                    .sumOf { it.forksCount }
            }
        }

    override fun getUserRepositoriesPaging(username: String): Flow<PagingData<Repository>> =
        Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE),
            pagingSourceFactory = { UserRepositoryPagingSource(githubService, username) },
        ).flow

    private suspend fun <T> safeApiCall(
        httpMapping: ((code: Int) -> GitPeekException?)? = null,
        block: suspend () -> T,
    ): Result<T> =
        runCatching { block() }
            .fold(
                onSuccess = { Result.success(it) },
                onFailure = { Result.failure(it.toGitPeekException(httpMapping)) },
            )
}
