package com.jayvijay.gitpeek.domain.usecase

import com.jayvijay.gitpeek.common.createTestUser
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UserNotFoundException
import com.jayvijay.gitpeek.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetUserUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = GetUserUseCase(repository)
    }

    @Test
    fun `should return success when repository returns user`() =
        runTest {
            val user = createTestUser()
            whenever(repository.getUser("testuser")).thenReturn(Result.success(user))

            val result = useCase("testuser")

            assertTrue(result.isSuccess)
            assertEquals(user, result.getOrThrow())
        }

    @Test
    fun `should delegate to repository with correct username`() =
        runTest {
            whenever(repository.getUser("octocat")).thenReturn(Result.success(createTestUser()))

            useCase("octocat")

            verify(repository).getUser("octocat")
        }

    @Test
    fun `should return failure when repository returns failure`() =
        runTest {
            val exception = UserNotFoundException("not found")
            whenever(repository.getUser("unknown")).thenReturn(Result.failure(exception))

            val result = useCase("unknown")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is UserNotFoundException)
        }

    @Test
    fun `should propagate NetworkException from repository`() =
        runTest {
            val exception = NetworkException("offline")
            whenever(repository.getUser("testuser")).thenReturn(Result.failure(exception))

            val result = useCase("testuser")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is NetworkException)
        }
}
