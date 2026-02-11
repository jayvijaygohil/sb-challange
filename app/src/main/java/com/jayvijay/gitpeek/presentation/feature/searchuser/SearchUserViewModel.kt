package com.jayvijay.gitpeek.presentation.feature.searchuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.usecase.GetUserRepositoriesUseCase
import com.jayvijay.gitpeek.domain.usecase.GetUserUseCase
import com.jayvijay.gitpeek.presentation.util.GitPeekState
import com.jayvijay.gitpeek.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@KoinViewModel
class SearchUserViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getUserRepositoriesUseCase: GetUserRepositoriesUseCase,
) : ViewModel(),
    ContainerHost<SearchUserState, SearchUserSideEffect> {
    override val container: Container<SearchUserState, SearchUserSideEffect> =
        container(GitPeekState.Idle)

    private val currentQuery = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val repositories: Flow<PagingData<Repository>> =
        currentQuery
            .flatMapLatest { query ->
                if (query == null) {
                    emptyFlow()
                } else {
                    getUserRepositoriesUseCase(query)
                }
            }.cachedIn(viewModelScope)

    fun searchUser(username: String) =
        intent {
            if (username.isBlank()) {
                reduce { GitPeekState.Idle }
                return@intent
            }

            reduce { GitPeekState.Loading }

            val result = getUserUseCase(username)

            result.fold(
                onSuccess = { user ->
                    currentQuery.emit(username)
                    reduce { GitPeekState.Success(user) }
                },
                onFailure = { error ->
                    reduce { GitPeekState.Error(error.toUiText()) }
                },
            )
        }

    fun onRepositoryClick(
        username: String,
        repositoryName: String,
    ) = intent {
        postSideEffect(SearchUserSideEffect.NavigateToRepositoryDetails(username, repositoryName))
    }

    fun resetSearch() =
        intent {
            currentQuery.emit(null)
            reduce { GitPeekState.Idle }
        }
}
