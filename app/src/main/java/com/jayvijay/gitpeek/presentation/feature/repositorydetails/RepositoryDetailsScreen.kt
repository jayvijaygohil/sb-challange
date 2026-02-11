package com.jayvijay.gitpeek.presentation.feature.repositorydetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.presentation.components.ErrorState
import com.jayvijay.gitpeek.presentation.components.LoadingState
import com.jayvijay.gitpeek.presentation.feature.repositorydetails.components.RepositoryDetailsContent
import com.jayvijay.gitpeek.presentation.navigation.base.AppNavigator
import com.jayvijay.gitpeek.presentation.util.GitPeekState
import org.koin.compose.activity.koinActivityInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailsScreen(
    username: String,
    repositoryName: String,
    totalRepoCount: Int,
    viewModel: RepositoryDetailsViewModel =
        koinViewModel {
            parametersOf(
                username,
                repositoryName,
                totalRepoCount,
            )
        },
    navigator: AppNavigator = koinActivityInject(),
) {
    val state by viewModel.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = repositoryName) },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (val repoState = state.repoState) {
                is GitPeekState.Idle,
                is GitPeekState.Loading,
                -> {
                    LoadingState()
                }

                is GitPeekState.Error -> {
                    ErrorState(message = repoState.message)
                }

                is GitPeekState.Success -> {
                    RepositoryDetailsContent(
                        repo = repoState.data,
                        forkCountState = state.forkCountState,
                    )
                }
            }
        }
    }
}
