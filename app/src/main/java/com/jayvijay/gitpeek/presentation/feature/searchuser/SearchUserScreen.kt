@file:OptIn(ExperimentalMaterial3Api::class)

package com.jayvijay.gitpeek.presentation.feature.searchuser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.presentation.components.ErrorState
import com.jayvijay.gitpeek.presentation.components.LoadingState
import com.jayvijay.gitpeek.presentation.feature.repositorydetails.RepositoryDetailsRoute
import com.jayvijay.gitpeek.presentation.feature.searchuser.components.SearchUserInput
import com.jayvijay.gitpeek.presentation.feature.searchuser.components.UserContent
import com.jayvijay.gitpeek.presentation.navigation.base.AppNavigator
import com.jayvijay.gitpeek.presentation.util.GitPeekState
import kotlinx.coroutines.launch
import org.koin.compose.activity.koinActivityInject
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SearchUserScreen(
    viewModel: SearchUserViewModel = koinViewModel(),
    navigator: AppNavigator = koinActivityInject(),
) {
    val state by viewModel.collectAsState()
    val repositories = viewModel.repositories.collectAsLazyPagingItems()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchUserSideEffect.NavigateToRepositoryDetails -> {
                val currentState = state
                if (currentState is GitPeekState.Success) {
                    navigator.navigateTo(
                        RepositoryDetailsRoute(
                            username = sideEffect.username,
                            repositoryName = sideEffect.repositoryName,
                            totalRepoCount = currentState.data.publicRepos,
                        ),
                    )
                }
            }
        }
    }

    BackHandler(enabled = state is GitPeekState.Success) {
        textFieldState.clearText()
        viewModel.resetSearch()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        SearchUserContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            repositories = repositories,
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = { query -> viewModel.searchUser(query) },
            onRepositoryClick = { repoName ->
                val currentState = state
                if (currentState is GitPeekState.Success) {
                    viewModel.onRepositoryClick(currentState.data.username, repoName)
                }
            },
        )
    }
}

@Composable
private fun SearchUserContent(
    modifier: Modifier,
    state: SearchUserState,
    repositories: LazyPagingItems<Repository>,
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onRepositoryClick: (repositoryName: String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
    ) {
        SearchBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            state = searchBarState,
            inputField = {
                SearchUserInput(
                    searchBarState = searchBarState,
                    textFieldState = textFieldState,
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        scope.launch { searchBarState.animateToCollapsed() }
                    },
                )
            },
        )

        when (state) {
            is GitPeekState.Idle -> {
                IdleState()
            }

            is GitPeekState.Loading -> {
                LoadingState()
            }

            is GitPeekState.Success -> {
                UserContent(
                    user = state.data,
                    repositories = repositories,
                    onRepositoryClick = onRepositoryClick,
                )
            }

            is GitPeekState.Error -> {
                ErrorState(message = state.message)
            }
        }
    }
}

@Composable
private fun IdleState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.search_user_idle_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
