package com.jayvijay.gitpeek.presentation.feature.repositorydetails

import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.presentation.util.GitPeekState

data class RepositoryDetailsState(
    val repoState: GitPeekState<Repository> = GitPeekState.Idle,
    val forkCountState: GitPeekState<Int> = GitPeekState.Idle,
)
