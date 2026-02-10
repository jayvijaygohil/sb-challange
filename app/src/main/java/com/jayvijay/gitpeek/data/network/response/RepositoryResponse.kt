package com.jayvijay.gitpeek.data.network.response

import com.jayvijay.gitpeek.domain.model.Repository
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryResponse(
    val id: Int,
    val name: String,
    val description: String?,
    @SerialName("full_name") val fullName: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
)

fun RepositoryResponse.toRepository() =
    Repository(
        id = id.toString(),
        name = name,
        description = description.orEmpty(),
        fullName = fullName,
        htmlUrl = htmlUrl,
        forksCount = forksCount,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
    )
