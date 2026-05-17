package com.thanh.githubrepoexplorer.presentation.model

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.Result
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.usecase.FetchRepoDetailsUseCase
import com.thanh.githubrepoexplorer.domain.usecase.GetRepositoriesUseCase
import com.thanh.githubrepoexplorer.domain.usecase.ToggleBookmarkUseCase
import com.thanh.githubrepoexplorer.util.TestDataGenerator.createRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepoViewModelTest {

    private lateinit var getRepositoriesUseCase: GetRepositoriesUseCase
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase
    private lateinit var fetchRepoDetailsUseCase: FetchRepoDetailsUseCase
    private lateinit var underTest: RepoViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRepositoriesUseCase = mockk()
        toggleBookmarkUseCase = mockk()
        fetchRepoDetailsUseCase = mockk()

        every { getRepositoriesUseCase(any()) } returns flowOf(PagingData.empty())

        underTest = RepoViewModel(
            getRepositoriesUseCase,
            toggleBookmarkUseCase,
            fetchRepoDetailsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        // Then
        assertThat(underTest.uiState.value).isEqualTo(RepoListUiState.Loading)
    }

    @Test
    fun `markReady should change state to Success`() = runTest {
        // When
        underTest.markReady()

        // Then
        assertThat(underTest.uiState.value).isInstanceOf(RepoListUiState.Success::class.java)
    }

    @Test
    fun `onAction SetSortOrder should update sortOrder in state`() = runTest {
        // Given
        underTest.markReady()
        val newOrder = SortOrder.STARS
        
        // When
        underTest.onAction(RepoListAction.SetSortOrder(newOrder))
        
        // Then
        val state = underTest.uiState.value as RepoListUiState.Success
        assertThat(state.sortOrder).isEqualTo(newOrder)
    }

    @Test
    fun `onAction ToggleGroupByLanguage should update groupByLanguage in state`() = runTest {
        // Given
        underTest.markReady()
        val initialState = (underTest.uiState.value as RepoListUiState.Success).groupByLanguage
        
        // When
        underTest.onAction(RepoListAction.ToggleGroupByLanguage)
        
        // Then
        val newState = (underTest.uiState.value as RepoListUiState.Success).groupByLanguage
        assertThat(newState).isEqualTo(!initialState)
    }

    @Test
    fun `onAction ToggleBookmark should call use case`() = runTest {
        // Given
        val repo = createRepo(1, "repo")
        coEvery { toggleBookmarkUseCase(repo) } returns Unit
        
        // When
        underTest.onAction(RepoListAction.ToggleBookmark(repo))
        advanceUntilIdle()
        
        // Then
        coVerify(exactly = 1) { toggleBookmarkUseCase(repo) }
    }

    @Test
    fun `fetchRepoDetails should emit error event when use case fails`() = runTest {
        // Given
        val repo = createRepo(1, "repo")
        val error = DataError.Network.NoInternet
        coEvery { fetchRepoDetailsUseCase(repo) } returns Result.Error(error)
        
        // When
        underTest.fetchRepoDetails(repo)
        
        // Then
        val event = underTest.events.first()
        assertThat(event).isInstanceOf(RepoListEvent.ShowDetailError::class.java)
        assertThat((event as RepoListEvent.ShowDetailError).error).isEqualTo(error)
    }

    @Suppress("UnusedFlow")
    @Test
    fun `repoPagingData should call getRepositoriesUseCase when state changes`() = runTest {
        // Given
        val items = mutableListOf<PagingData<Repo>>()
        val job = launch {
            underTest.repoPagingData.collect { items.add(it) }
        }

        // When
        underTest.markReady()
        underTest.onAction(RepoListAction.SetSortOrder(SortOrder.STARS))
        advanceUntilIdle()

        // Then
        verify(atLeast = 1) { getRepositoriesUseCase(any()) }
        job.cancel()
    }
}
