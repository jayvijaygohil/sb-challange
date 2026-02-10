package com.jayvijay.gitpeek.data.network

import com.jayvijay.gitpeek.data.network.response.RepositoryResponse
import com.jayvijay.gitpeek.data.network.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("/users/{user_name}")
    suspend fun getUser(
        @Path("user_name") userName: String,
    ): UserResponse

    @GET("/users/{user_name}/repos")
    suspend fun getUserRepos(
        @Path("user_name") userName: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
    ): List<RepositoryResponse>

    @GET("/repos/{user_name}/{repo_name}")
    suspend fun getRepository(
        @Path("user_name") userName: String,
        @Path("repo_name") repoName: String,
    ): RepositoryResponse

    companion object {
        internal const val BASE_URL = "https://api.github.com"
    }
}
