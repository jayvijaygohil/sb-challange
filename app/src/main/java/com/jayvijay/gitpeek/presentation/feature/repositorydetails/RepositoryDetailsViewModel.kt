package com.jayvijay.gitpeek.presentation.feature.repositorydetails

import androidx.lifecycle.ViewModel
import com.jayvijay.gitpeek.domain.usecase.GetRepositoryUseCase
import com.jayvijay.gitpeek.domain.usecase.GetUserForkCountUseCase
import com.jayvijay.gitpeek.presentation.util.GitPeekState
import com.jayvijay.gitpeek.presentation.util.toUiText
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@KoinViewModel
class RepositoryDetailsViewModel(
    @InjectedParam private val username: String,
    @InjectedParam private val repositoryName: String,
    @InjectedParam private val totalRepoCount: Int,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getUserForkCountUseCase: GetUserForkCountUseCase,
) : ViewModel(),
    ContainerHost<RepositoryDetailsState, Nothing> {
    override val container: Container<RepositoryDetailsState, Nothing> =
        container(RepositoryDetailsState()) {
            loadRepository()
            loadForkCount()
        }

    fun loadRepository() =
        intent {
            reduce { state.copy(repoState = GitPeekState.Loading) }

            val result = getRepositoryUseCase(username, repositoryName)

            result.fold(
                onSuccess = { repo ->
                    reduce { state.copy(repoState = GitPeekState.Success(repo)) }
                },
                onFailure = { error ->
                    reduce { state.copy(repoState = GitPeekState.Error(error.toUiText())) }
                },
            )
        }

    internal fun loadForkCount() =
        intent {
            reduce { state.copy(forkCountState = GitPeekState.Loading) }

            val result = getUserForkCountUseCase(username, totalRepoCount)

            result.fold(
                onSuccess = { count ->
                    reduce { state.copy(forkCountState = GitPeekState.Success(count)) }
                },
                onFailure = { error ->
                    reduce { state.copy(forkCountState = GitPeekState.Error(error.toUiText())) }
                },
            )
        }
}
