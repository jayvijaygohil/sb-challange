package com.jayvijay.gitpeek.common

import com.jayvijay.gitpeek.data.network.response.RepositoryResponse
import com.jayvijay.gitpeek.data.network.response.UserResponse
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.model.User

fun createTestUser(
    id: String = "123",
    name: String = "Test User",
    username: String = "testuser",
    avatarUrl: String = "https://example.com/avatar.png",
    publicRepos: Int = 10,
) = User(
    id = id,
    name = name,
    username = username,
    avatarUrl = avatarUrl,
    publicRepos = publicRepos,
)

fun createTestRepository(
    id: String = "1",
    name: String = "test-repo",
    description: String = "Test repository",
    fullName: String = "testuser/test-repo",
    htmlUrl: String = "https://github.com/testuser/test-repo",
    forksCount: Int = 5,
    stargazersCount: Int = 10,
    watchersCount: Int = 3,
) = Repository(
    id = id,
    name = name,
    description = description,
    fullName = fullName,
    htmlUrl = htmlUrl,
    forksCount = forksCount,
    stargazersCount = stargazersCount,
    watchersCount = watchersCount,
)

fun createUserResponse(
    id: Int = 123,
    name: String? = "Test User",
    username: String? = "testuser",
    avatarUrl: String = "https://example.com/avatar.png",
    publicRepos: Int = 10,
) = UserResponse(
    id = id,
    name = name,
    username = username,
    avatarUrl = avatarUrl,
    publicRepos = publicRepos,
)

fun createRepositoryResponse(
    id: Int = 1,
    name: String = "test-repo",
    description: String? = "Test repository",
    fullName: String = "testuser/test-repo",
    htmlUrl: String = "https://github.com/testuser/test-repo",
    forksCount: Int = 5,
    stargazersCount: Int = 10,
    watchersCount: Int = 3,
) = RepositoryResponse(
    id = id,
    name = name,
    description = description,
    fullName = fullName,
    htmlUrl = htmlUrl,
    forksCount = forksCount,
    stargazersCount = stargazersCount,
    watchersCount = watchersCount,
)
