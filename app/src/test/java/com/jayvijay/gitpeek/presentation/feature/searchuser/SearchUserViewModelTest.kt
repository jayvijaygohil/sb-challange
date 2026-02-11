package com.jayvijay.gitpeek.presentation.feature.searchuser

import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.common.createTestUser
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.RateLimitException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UserNotFoundException
import com.jayvijay.gitpeek.domain.usecase.GetUserRepositoriesUseCase
import com.jayvijay.gitpeek.domain.usecase.GetUserUseCase
import com.jayvijay.gitpeek.presentation.util.GitPeekState
import com.jayvijay.gitpeek.presentation.util.UiText
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.orbitmvi.orbit.test.test

class SearchUserViewModelTest {
    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var getUserRepositoriesUseCase: GetUserRepositoriesUseCase

    @Before
    fun setup() {
        getUserUseCase = mock()
        getUserRepositoriesUseCase = mock()
    }

    private fun createViewModel() = SearchUserViewModel(getUserUseCase, getUserRepositoriesUseCase)

    @Test
    fun `should transition to Loading then Success when search succeeds`() =
        runTest {
            val user = createTestUser()
            whenever(getUserUseCase("testuser")).thenReturn(Result.success(user))

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("testuser")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Success(user) }
            }
        }

    @Test
    fun `should call getUserUseCase with correct username`() =
        runTest {
            whenever(getUserUseCase("octocat")).thenReturn(Result.success(createTestUser()))

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("octocat")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Success(createTestUser()) }
            }

            verify(getUserUseCase).invoke("octocat")
        }

    @Test
    fun `should transition to Loading then Error when search fails`() =
        runTest {
            whenever(getUserUseCase("unknown")).thenReturn(
                Result.failure(UserNotFoundException("not found")),
            )

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("unknown")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Error(UiText.StringResource(R.string.error_user_not_found)) }
            }
        }

    @Test
    fun `should show network error when NetworkException occurs`() =
        runTest {
            whenever(getUserUseCase("testuser")).thenReturn(
                Result.failure(NetworkException("offline")),
            )

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("testuser")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Error(UiText.StringResource(R.string.error_network)) }
            }
        }

    @Test
    fun `should show rate limit error when RateLimitException occurs`() =
        runTest {
            whenever(getUserUseCase("testuser")).thenReturn(
                Result.failure(RateLimitException("rate limited")),
            )

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("testuser")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Error(UiText.StringResource(R.string.error_rate_limit)) }
            }
        }

    @Test
    fun `should show unknown error when generic exception occurs`() =
        runTest {
            whenever(getUserUseCase("testuser")).thenReturn(
                Result.failure(RuntimeException("unexpected")),
            )

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("testuser")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Error(UiText.StringResource(R.string.error_unknown)) }
            }
        }

    @Test
    fun `should not call use case when username is blank`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("")
            }

            verify(getUserUseCase, never()).invoke(any())
        }

    @Test
    fun `should remain Idle when username is blank`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("   ")
            }

            verify(getUserUseCase, never()).invoke(any())
        }

    @Test
    fun `should reset to Idle state when resetSearch is called`() =
        runTest {
            val user = createTestUser()
            whenever(getUserUseCase("testuser")).thenReturn(Result.success(user))

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("testuser")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Success(user) }

                containerHost.resetSearch()

                expectState { GitPeekState.Idle }
            }
        }

    @Test
    fun `should post NavigateToRepositoryDetails side effect on repository click`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.onRepositoryClick("testuser", "test-repo")

                expectSideEffect(
                    SearchUserSideEffect.NavigateToRepositoryDetails("testuser", "test-repo"),
                )
            }
        }

    @Test
    fun `should post side effect with correct username and repository name`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.onRepositoryClick("octocat", "hello-world")

                expectSideEffect(
                    SearchUserSideEffect.NavigateToRepositoryDetails("octocat", "hello-world"),
                )
            }
        }

    @Test
    fun `should handle consecutive searches correctly`() =
        runTest {
            val user1 = createTestUser(id = "1", name = "User 1")
            val user2 = createTestUser(id = "2", name = "User 2")
            whenever(getUserUseCase("user1")).thenReturn(Result.success(user1))
            whenever(getUserUseCase("user2")).thenReturn(Result.success(user2))

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("user1")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Success(user1) }

                containerHost.searchUser("user2")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Success(user2) }
            }
        }

    @Test
    fun `should handle search after error state`() =
        runTest {
            val user = createTestUser()
            whenever(getUserUseCase("bad")).thenReturn(Result.failure(RuntimeException("fail")))
            whenever(getUserUseCase("good")).thenReturn(Result.success(user))

            val viewModel = createViewModel()
            viewModel.test(this) {
                containerHost.searchUser("bad")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Error(UiText.StringResource(R.string.error_unknown)) }

                containerHost.searchUser("good")

                expectState { GitPeekState.Loading }
                expectState { GitPeekState.Success(user) }
            }
        }
}
