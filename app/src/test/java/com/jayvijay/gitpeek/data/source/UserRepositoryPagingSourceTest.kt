package com.jayvijay.gitpeek.data.source

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jayvijay.gitpeek.common.createRepositoryResponse
import com.jayvijay.gitpeek.data.network.GithubService
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.RateLimitException
import com.jayvijay.gitpeek.domain.model.GitPeekException.ServerException
import com.jayvijay.gitpeek.domain.model.Repository
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UserRepositoryPagingSourceTest {
    private lateinit var githubService: GithubService
    private lateinit var pagingSource: UserRepositoryPagingSource

    private val username = "testuser"

    @Before
    fun setup() {
        githubService = mock()
        pagingSource = UserRepositoryPagingSource(githubService, username)
    }

    @Test
    fun `should return Page with data when first page loads successfully`() =
        runTest {
            val responses =
                listOf(
                    createRepositoryResponse(id = 1, name = "repo-1"),
                    createRepositoryResponse(id = 2, name = "repo-2"),
                )
            whenever(githubService.getUserRepos(username, 10, 1)).thenReturn(responses)

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                )

            assertTrue(result is PagingSource.LoadResult.Page)
            val page = result as PagingSource.LoadResult.Page
            assertEquals(2, page.data.size)
            assertEquals("repo-1", page.data[0].name)
            assertEquals("repo-2", page.data[1].name)
        }

    @Test
    fun `should have null prevKey for first page`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 1)).thenReturn(
                listOf(createRepositoryResponse()),
            )

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                ) as PagingSource.LoadResult.Page

            assertNull(result.prevKey)
        }

    @Test
    fun `should have nextKey as page plus one when data is non-empty`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 1)).thenReturn(
                listOf(createRepositoryResponse()),
            )

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                ) as PagingSource.LoadResult.Page

            assertEquals(2, result.nextKey)
        }

    @Test
    fun `should have null nextKey when response is empty`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 1)).thenReturn(emptyList())

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                ) as PagingSource.LoadResult.Page

            assertNull(result.nextKey)
        }

    @Test
    fun `should use provided key as page number`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 3)).thenReturn(
                listOf(createRepositoryResponse()),
            )

            pagingSource.load(
                PagingSource.LoadParams.Append(key = 3, loadSize = 10, placeholdersEnabled = false),
            )

            verify(githubService).getUserRepos(username, 10, 3)
        }

    @Test
    fun `should have prevKey as page minus one for non-first pages`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 3)).thenReturn(
                listOf(createRepositoryResponse()),
            )

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Append(key = 3, loadSize = 10, placeholdersEnabled = false),
                ) as PagingSource.LoadResult.Page

            assertEquals(2, result.prevKey)
        }

    @Test
    fun `should correctly map RepositoryResponse to Repository domain model`() =
        runTest {
            val response =
                createRepositoryResponse(
                    id = 42,
                    name = "my-repo",
                    description = "A cool repo",
                    fullName = "testuser/my-repo",
                    htmlUrl = "https://github.com/testuser/my-repo",
                    forksCount = 15,
                    stargazersCount = 100,
                    watchersCount = 50,
                )
            whenever(githubService.getUserRepos(username, 10, 1)).thenReturn(listOf(response))

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                ) as PagingSource.LoadResult.Page

            val repo = result.data.first()
            assertEquals("42", repo.id)
            assertEquals("my-repo", repo.name)
            assertEquals("A cool repo", repo.description)
            assertEquals("testuser/my-repo", repo.fullName)
            assertEquals(15, repo.forksCount)
            assertEquals(100, repo.stargazersCount)
        }

    @Test
    fun `should pass loadSize to perPage parameter`() =
        runTest {
            whenever(githubService.getUserRepos(username, 25, 1)).thenReturn(emptyList())

            pagingSource.load(
                PagingSource.LoadParams.Refresh(key = null, loadSize = 25, placeholdersEnabled = false),
            )

            verify(githubService).getUserRepos(username, 25, 1)
        }

    @Test
    fun `should return Error with NetworkException when IOException occurs`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 1)).thenAnswer { throw IOException("offline") }

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                )

            assertTrue(result is PagingSource.LoadResult.Error)
            val error = result as PagingSource.LoadResult.Error
            assertTrue(error.throwable is NetworkException)
        }

    @Test
    fun `should return Error with ServerException when 404 HttpException occurs`() =
        runTest {
            val httpException = createHttpException(404)
            whenever(githubService.getUserRepos(username, 10, 1)).thenThrow(httpException)

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                )

            assertTrue(result is PagingSource.LoadResult.Error)
            val error = result as PagingSource.LoadResult.Error
            assertTrue(error.throwable is ServerException)
            assertEquals(404, (error.throwable as ServerException).code)
        }

    @Test
    fun `should return Error with RateLimitException when 403 HttpException occurs`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 1)).thenThrow(createHttpException(403))

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                )

            assertTrue(result is PagingSource.LoadResult.Error)
            assertTrue((result as PagingSource.LoadResult.Error).throwable is RateLimitException)
        }

    @Test
    fun `should return Error with ServerException when 500 HttpException occurs`() =
        runTest {
            whenever(githubService.getUserRepos(username, 10, 1)).thenThrow(createHttpException(500))

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
                )

            assertTrue(result is PagingSource.LoadResult.Error)
            val error = (result as PagingSource.LoadResult.Error).throwable as ServerException
            assertEquals(500, error.code)
        }

    @Test
    fun `should return null when anchorPosition is null`() {
        val state =
            PagingState<Int, Repository>(
                pages = emptyList(),
                anchorPosition = null,
                config = PagingConfig(pageSize = 10),
                leadingPlaceholderCount = 0,
            )

        val result = pagingSource.getRefreshKey(state)

        assertNull(result)
    }

    private fun createHttpException(code: Int): HttpException {
        val response = Response.error<Any>(code, "error".toResponseBody(null))
        return HttpException(response)
    }
}
