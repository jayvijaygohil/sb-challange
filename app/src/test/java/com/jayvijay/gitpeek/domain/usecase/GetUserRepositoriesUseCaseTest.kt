package com.jayvijay.gitpeek.domain.usecase

import androidx.paging.PagingData
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetUserRepositoriesUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserRepositoriesUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = GetUserRepositoriesUseCase(repository)
    }

    @Test
    fun `should delegate to repository with correct username`() {
        whenever(repository.getUserRepositoriesPaging("testuser")).thenReturn(emptyFlow())

        useCase("testuser")

        verify(repository).getUserRepositoriesPaging("testuser")
    }

    @Test
    fun `should return flow from repository`() {
        val expectedFlow: Flow<PagingData<Repository>> = flowOf(PagingData.empty())
        whenever(repository.getUserRepositoriesPaging("testuser")).thenReturn(expectedFlow)

        val result = useCase("testuser")

        assertNotNull(result)
    }

    @Test
    fun `should call repository with different usernames`() {
        whenever(repository.getUserRepositoriesPaging("octocat")).thenReturn(emptyFlow())

        useCase("octocat")

        verify(repository).getUserRepositoriesPaging("octocat")
    }
}
