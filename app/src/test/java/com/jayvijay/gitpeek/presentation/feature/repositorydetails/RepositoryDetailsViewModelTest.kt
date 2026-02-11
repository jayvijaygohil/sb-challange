package com.jayvijay.gitpeek.presentation.feature.repositorydetails

import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.common.createTestRepository
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.ServerException
import com.jayvijay.gitpeek.domain.usecase.GetRepositoryUseCase
import com.jayvijay.gitpeek.domain.usecase.GetUserForkCountUseCase
import com.jayvijay.gitpeek.presentation.util.GitPeekState
import com.jayvijay.gitpeek.presentation.util.UiText
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.orbitmvi.orbit.test.test

class RepositoryDetailsViewModelTest {
    private lateinit var getRepositoryUseCase: GetRepositoryUseCase
    private lateinit var getUserForkCountUseCase: GetUserForkCountUseCase

    private val username = "testuser"
    private val repositoryName = "test-repo"
    private val totalRepoCount = 10

    private val initialState = RepositoryDetailsState()

    @Before
    fun setup() {
        getRepositoryUseCase = mock()
        getUserForkCountUseCase = mock()
    }

    private fun createViewModel() =
        RepositoryDetailsViewModel(
            username = username,
            repositoryName = repositoryName,
            totalRepoCount = totalRepoCount,
            getRepositoryUseCase = getRepositoryUseCase,
            getUserForkCountUseCase = getUserForkCountUseCase,
        )

    @Test
    fun `should load repository and fork count successfully in parallel`() =
        runTest {
            val repo = createTestRepository()
            whenever(getRepositoryUseCase(username, repositoryName)).thenReturn(Result.success(repo))
            whenever(getUserForkCountUseCase(username, totalRepoCount)).thenReturn(Result.success(42))

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadRepository()
                expectState { copy(repoState = GitPeekState.Loading) }
                expectState { copy(repoState = GitPeekState.Success(repo)) }

                containerHost.loadForkCount()
                expectState { copy(forkCountState = GitPeekState.Loading) }
                expectState { copy(forkCountState = GitPeekState.Success(42)) }
            }
        }

    @Test
    fun `should call use cases with correct parameters`() =
        runTest {
            val repo = createTestRepository()
            whenever(getRepositoryUseCase(username, repositoryName)).thenReturn(Result.success(repo))
            whenever(getUserForkCountUseCase(username, totalRepoCount)).thenReturn(Result.success(0))

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadRepository()
                expectState { copy(repoState = GitPeekState.Loading) }
                expectState { copy(repoState = GitPeekState.Success(repo)) }

                containerHost.loadForkCount()
                expectState { copy(forkCountState = GitPeekState.Loading) }
                expectState { copy(forkCountState = GitPeekState.Success(0)) }
            }

            verify(getRepositoryUseCase).invoke(username, repositoryName)
            verify(getUserForkCountUseCase).invoke(username, totalRepoCount)
        }

    @Test
    fun `should show repo error when repository fetch fails`() =
        runTest {
            whenever(getRepositoryUseCase(username, repositoryName)).thenReturn(
                Result.failure(ServerException(404, "not found")),
            )

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadRepository()
                expectState { copy(repoState = GitPeekState.Loading) }
                expectState { copy(repoState = GitPeekState.Error(UiText.StringResource(R.string.error_server, listOf(404)))) }
            }
        }

    @Test
    fun `should show network error when repository fetch throws NetworkException`() =
        runTest {
            whenever(getRepositoryUseCase(username, repositoryName)).thenReturn(
                Result.failure(NetworkException("offline")),
            )

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadRepository()
                expectState { copy(repoState = GitPeekState.Loading) }
                expectState { copy(repoState = GitPeekState.Error(UiText.StringResource(R.string.error_network))) }
            }
        }

    @Test
    fun `should show fork count error when fork count fetch fails`() =
        runTest {
            whenever(getUserForkCountUseCase(username, totalRepoCount)).thenReturn(
                Result.failure(NetworkException("timeout")),
            )

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadForkCount()
                expectState { copy(forkCountState = GitPeekState.Loading) }
                expectState { copy(forkCountState = GitPeekState.Error(UiText.StringResource(R.string.error_network))) }
            }
        }

    @Test
    fun `should show unknown error for fork count when generic exception occurs`() =
        runTest {
            whenever(getUserForkCountUseCase(username, totalRepoCount)).thenReturn(
                Result.failure(RuntimeException("unexpected")),
            )

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadForkCount()
                expectState { copy(forkCountState = GitPeekState.Loading) }
                expectState { copy(forkCountState = GitPeekState.Error(UiText.StringResource(R.string.error_unknown))) }
            }
        }

    @Test
    fun `should show errors for both when both fetches fail`() =
        runTest {
            whenever(getRepositoryUseCase(username, repositoryName)).thenReturn(
                Result.failure(ServerException(500, "server error")),
            )
            whenever(getUserForkCountUseCase(username, totalRepoCount)).thenReturn(
                Result.failure(NetworkException("offline")),
            )

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadRepository()
                expectState { copy(repoState = GitPeekState.Loading) }
                expectState { copy(repoState = GitPeekState.Error(UiText.StringResource(R.string.error_server, listOf(500)))) }

                containerHost.loadForkCount()
                expectState { copy(forkCountState = GitPeekState.Loading) }
                expectState { copy(forkCountState = GitPeekState.Error(UiText.StringResource(R.string.error_network))) }
            }
        }

    @Test
    fun `should update repo state when loadRepository is called explicitly`() =
        runTest {
            val repo = createTestRepository()
            whenever(getRepositoryUseCase(username, repositoryName))
                .thenReturn(Result.failure(ServerException(500, "error")))
                .thenReturn(Result.success(repo))

            val viewModel = createViewModel()
            viewModel.test(this, initialState = initialState) {
                containerHost.loadRepository()
                expectState { copy(repoState = GitPeekState.Loading) }
                expectState { copy(repoState = GitPeekState.Error(UiText.StringResource(R.string.error_server, listOf(500)))) }

                containerHost.loadRepository()
                expectState { copy(repoState = GitPeekState.Loading) }
                expectState { copy(repoState = GitPeekState.Success(repo)) }
            }
        }
}
