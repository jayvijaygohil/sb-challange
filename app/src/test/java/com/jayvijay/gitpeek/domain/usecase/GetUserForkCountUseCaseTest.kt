package com.jayvijay.gitpeek.domain.usecase

import com.jayvijay.gitpeek.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException

class GetUserForkCountUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserForkCountUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = GetUserForkCountUseCase(repository)
    }

    @Test
    fun `should return success with fork count when repository succeeds`() =
        runTest {
            whenever(repository.getUserForkCount("testuser", 10)).thenReturn(Result.success(42))

            val result = useCase("testuser", 10)

            assertTrue(result.isSuccess)
            assertEquals(42, result.getOrThrow())
        }

    @Test
    fun `should return success with zero when repository returns zero`() =
        runTest {
            whenever(repository.getUserForkCount("testuser", 0)).thenReturn(Result.success(0))

            val result = useCase("testuser", 0)

            assertTrue(result.isSuccess)
            assertEquals(0, result.getOrThrow())
        }

    @Test
    fun `should delegate to repository with correct parameters`() =
        runTest {
            whenever(repository.getUserForkCount("octocat", 50)).thenReturn(Result.success(100))

            useCase("octocat", 50)

            verify(repository).getUserForkCount("octocat", 50)
        }

    @Test
    fun `should return failure when repository returns failure`() =
        runTest {
            whenever(repository.getUserForkCount("testuser", 10)).thenReturn(Result.failure(IOException("network error")))

            val result = useCase("testuser", 10)

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IOException)
        }

    @Test
    fun `should propagate failure result from repository`() =
        runTest {
            whenever(repository.getUserForkCount("testuser", 5)).thenReturn(Result.failure(RuntimeException("unexpected")))

            val result = useCase("testuser", 5)

            assertTrue(result.isFailure)
            assertEquals("unexpected", result.exceptionOrNull()?.message)
        }
}
