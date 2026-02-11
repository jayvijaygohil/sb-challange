package com.jayvijay.gitpeek.domain.usecase

import com.jayvijay.gitpeek.common.createTestRepository
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.ServerException
import com.jayvijay.gitpeek.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetRepositoryUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetRepositoryUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = GetRepositoryUseCase(repository)
    }

    @Test
    fun `should return success when repository returns repository`() =
        runTest {
            val repo = createTestRepository()
            whenever(repository.getRepository("testuser", "test-repo")).thenReturn(Result.success(repo))

            val result = useCase("testuser", "test-repo")

            assertTrue(result.isSuccess)
            assertEquals(repo, result.getOrThrow())
        }

    @Test
    fun `should delegate to repository with correct parameters`() =
        runTest {
            whenever(repository.getRepository("user", "repo")).thenReturn(
                Result.success(createTestRepository()),
            )

            useCase("user", "repo")

            verify(repository).getRepository("user", "repo")
        }

    @Test
    fun `should return failure when repository returns failure`() =
        runTest {
            val exception = ServerException(404, "not found")
            whenever(repository.getRepository("user", "repo")).thenReturn(Result.failure(exception))

            val result = useCase("user", "repo")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is ServerException)
        }

    @Test
    fun `should propagate NetworkException from repository`() =
        runTest {
            val exception = NetworkException("offline")
            whenever(repository.getRepository("user", "repo")).thenReturn(Result.failure(exception))

            val result = useCase("user", "repo")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is NetworkException)
        }
}
