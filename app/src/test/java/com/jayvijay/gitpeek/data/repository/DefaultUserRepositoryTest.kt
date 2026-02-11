package com.jayvijay.gitpeek.data.repository

import com.jayvijay.gitpeek.common.createRepositoryResponse
import com.jayvijay.gitpeek.common.createUserResponse
import com.jayvijay.gitpeek.data.network.GithubService
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.RateLimitException
import com.jayvijay.gitpeek.domain.model.GitPeekException.ServerException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UserNotFoundException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DefaultUserRepositoryTest {
    private lateinit var githubService: GithubService
    private lateinit var repository: DefaultUserRepository

    @Before
    fun setup() {
        githubService = mock()
        repository = DefaultUserRepository(githubService)
    }

    @Test
    fun `should return success with user when getUser succeeds`() =
        runTest {
            val userResponse = createUserResponse()
            whenever(githubService.getUser("testuser")).thenReturn(userResponse)

            val result = repository.getUser("testuser")

            assertTrue(result.isSuccess)
            val user = result.getOrThrow()
            assertEquals("123", user.id)
            assertEquals("Test User", user.name)
            assertEquals("testuser", user.username)
            assertEquals(10, user.publicRepos)
        }

    @Test
    fun `should call githubService getUser with correct username`() =
        runTest {
            whenever(githubService.getUser("octocat")).thenReturn(createUserResponse())

            repository.getUser("octocat")

            verify(githubService).getUser("octocat")
        }

    @Test
    fun `should map null name and username to empty strings`() =
        runTest {
            val userResponse = createUserResponse(name = null, username = null)
            whenever(githubService.getUser("testuser")).thenReturn(userResponse)

            val result = repository.getUser("testuser")

            assertTrue(result.isSuccess)
            assertEquals("", result.getOrThrow().name)
            assertEquals("", result.getOrThrow().username)
        }

    @Test
    fun `should return failure with UserNotFoundException when getUser returns 404`() =
        runTest {
            whenever(githubService.getUser("unknown")).thenThrow(createHttpException(404))

            val result = repository.getUser("unknown")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is UserNotFoundException)
            assertEquals("User not found", result.exceptionOrNull()?.message)
        }

    @Test
    fun `should return failure with RateLimitException when getUser returns 403`() =
        runTest {
            whenever(githubService.getUser("testuser")).thenThrow(createHttpException(403))

            val result = repository.getUser("testuser")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is RateLimitException)
        }

    @Test
    fun `should return failure with RateLimitException when getUser returns 429`() =
        runTest {
            whenever(githubService.getUser("testuser")).thenThrow(createHttpException(429))

            val result = repository.getUser("testuser")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is RateLimitException)
        }

    @Test
    fun `should return failure with NetworkException when getUser throws IOException`() =
        runTest {
            whenever(githubService.getUser("testuser")).thenAnswer { throw IOException("no connection") }

            val result = repository.getUser("testuser")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is NetworkException)
        }

    @Test
    fun `should return failure with ServerException when getUser returns 500`() =
        runTest {
            whenever(githubService.getUser("testuser")).thenThrow(createHttpException(500))

            val result = repository.getUser("testuser")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is ServerException)
            assertEquals(500, (result.exceptionOrNull() as ServerException).code)
        }

    @Test
    fun `should return success with repository when getRepository succeeds`() =
        runTest {
            val repoResponse = createRepositoryResponse()
            whenever(githubService.getRepository("testuser", "test-repo")).thenReturn(repoResponse)

            val result = repository.getRepository("testuser", "test-repo")

            assertTrue(result.isSuccess)
            val repo = result.getOrThrow()
            assertEquals("1", repo.id)
            assertEquals("test-repo", repo.name)
            assertEquals("Test repository", repo.description)
            assertEquals(5, repo.forksCount)
        }

    @Test
    fun `should call githubService getRepository with correct parameters`() =
        runTest {
            whenever(githubService.getRepository("user", "repo")).thenReturn(createRepositoryResponse())

            repository.getRepository("user", "repo")

            verify(githubService).getRepository("user", "repo")
        }

    @Test
    fun `should map null description to empty string`() =
        runTest {
            val repoResponse = createRepositoryResponse(description = null)
            whenever(githubService.getRepository("testuser", "test-repo")).thenReturn(repoResponse)

            val result = repository.getRepository("testuser", "test-repo")

            assertEquals("", result.getOrThrow().description)
        }

    @Test
    fun `should return failure with ServerException when getRepository returns 404`() =
        runTest {
            whenever(githubService.getRepository("testuser", "unknown")).thenThrow(createHttpException(404))

            val result = repository.getRepository("testuser", "unknown")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is ServerException)
            assertEquals(404, (result.exceptionOrNull() as ServerException).code)
        }

    @Test
    fun `should return failure with RateLimitException when getRepository returns 403`() =
        runTest {
            whenever(githubService.getRepository("testuser", "repo")).thenThrow(createHttpException(403))

            val result = repository.getRepository("testuser", "repo")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is RateLimitException)
        }

    @Test
    fun `should return failure with NetworkException when getRepository throws IOException`() =
        runTest {
            whenever(githubService.getRepository("testuser", "repo")).thenAnswer { throw IOException("offline") }

            val result = repository.getRepository("testuser", "repo")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is NetworkException)
        }

    @Test
    fun `should return zero when totalRepositoryCount is zero`() =
        runTest {
            val result = repository.getUserForkCount("testuser", 0)

            assertTrue(result.isSuccess)
            assertEquals(0, result.getOrThrow())
        }

    @Test
    fun `should return sum of all fork counts for single page`() =
        runTest {
            val repos =
                listOf(
                    createRepositoryResponse(id = 1, forksCount = 10),
                    createRepositoryResponse(id = 2, forksCount = 20),
                    createRepositoryResponse(id = 3, forksCount = 30),
                )
            whenever(githubService.getUserRepos("testuser", 100, 1)).thenReturn(repos)

            val result = repository.getUserForkCount("testuser", 3)

            assertTrue(result.isSuccess)
            assertEquals(60, result.getOrThrow())
        }

    @Test
    fun `should fetch multiple pages and sum all fork counts`() =
        runTest {
            val page1 = listOf(createRepositoryResponse(id = 1, forksCount = 50))
            val page2 = listOf(createRepositoryResponse(id = 2, forksCount = 30))
            whenever(githubService.getUserRepos("testuser", 100, 1)).thenReturn(page1)
            whenever(githubService.getUserRepos("testuser", 100, 2)).thenReturn(page2)

            val result = repository.getUserForkCount("testuser", 101)

            assertTrue(result.isSuccess)
            assertEquals(80, result.getOrThrow())
        }

    @Test
    fun `should calculate correct number of pages`() =
        runTest {
            whenever(githubService.getUserRepos("testuser", 100, 1)).thenReturn(emptyList())
            whenever(githubService.getUserRepos("testuser", 100, 2)).thenReturn(emptyList())
            whenever(githubService.getUserRepos("testuser", 100, 3)).thenReturn(emptyList())

            repository.getUserForkCount("testuser", 201)

            verify(githubService).getUserRepos("testuser", 100, 1)
            verify(githubService).getUserRepos("testuser", 100, 2)
            verify(githubService).getUserRepos("testuser", 100, 3)
        }

    @Test
    fun `should return failure with ServerException when getUserForkCount fails`() =
        runTest {
            whenever(githubService.getUserRepos("testuser", 100, 1)).thenThrow(createHttpException(500))

            val result = repository.getUserForkCount("testuser", 5)

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is ServerException)
        }

    private fun createHttpException(code: Int): HttpException {
        val response = Response.error<Any>(code, "error".toResponseBody(null))
        return HttpException(response)
    }
}
