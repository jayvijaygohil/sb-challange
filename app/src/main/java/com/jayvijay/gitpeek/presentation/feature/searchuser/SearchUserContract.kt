package com.jayvijay.gitpeek.presentation.feature.searchuser

import com.jayvijay.gitpeek.domain.model.User
import com.jayvijay.gitpeek.presentation.util.GitPeekState

typealias SearchUserState = GitPeekState<User>

sealed class SearchUserSideEffect {
    data class NavigateToRepositoryDetails(
        val username: String,
        val repositoryName: String,
    ) : SearchUserSideEffect()
}
