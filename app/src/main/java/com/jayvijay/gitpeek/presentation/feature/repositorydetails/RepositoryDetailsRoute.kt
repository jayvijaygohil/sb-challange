package com.jayvijay.gitpeek.presentation.feature.repositorydetails

import com.jayvijay.gitpeek.presentation.navigation.base.AppRoute
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryDetailsRoute(
    val username: String,
    val repositoryName: String,
    val totalRepoCount: Int,
) : AppRoute
