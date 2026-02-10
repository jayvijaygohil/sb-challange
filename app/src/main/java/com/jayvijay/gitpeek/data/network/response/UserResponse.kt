package com.jayvijay.gitpeek.data.network.response

import com.jayvijay.gitpeek.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val name: String?,
    @SerialName("login")
    val username: String?,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("public_repos")
    val publicRepos: Int,
)

fun UserResponse.toUser() =
    User(
        id = id.toString(),
        name = name.orEmpty(),
        username = username.orEmpty(),
        avatarUrl = avatarUrl,
        publicRepos = publicRepos,
    )
