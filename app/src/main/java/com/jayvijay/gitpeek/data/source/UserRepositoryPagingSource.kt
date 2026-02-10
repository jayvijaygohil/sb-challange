package com.jayvijay.gitpeek.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jayvijay.gitpeek.data.mapper.toGitPeekException
import com.jayvijay.gitpeek.data.network.GithubService
import com.jayvijay.gitpeek.data.network.response.toRepository
import com.jayvijay.gitpeek.domain.model.Repository

class UserRepositoryPagingSource(
    private val service: GithubService,
    private val username: String,
) : PagingSource<Int, Repository>() {
    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        val page = params.key ?: 1
        return try {
            val response =
                service.getUserRepos(
                    userName = username,
                    perPage = params.loadSize,
                    page = page,
                )
            val repositories = response.map { it.toRepository() }

            LoadResult.Page(
                data = repositories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (repositories.isEmpty()) null else page + 1,
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception.toGitPeekException())
        }
    }
}
