package com.thanh.githubrepoexplorer.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.domain.model.Result
import com.thanh.githubrepoexplorer.domain.usecase.FetchRepoDetailsUseCase
import com.thanh.githubrepoexplorer.domain.usecase.GetRepositoriesUseCase
import com.thanh.githubrepoexplorer.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val fetchRepoDetailsUseCase: FetchRepoDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepoListUiState>(RepoListUiState.Loading)

    val uiState: StateFlow<RepoListUiState> = _uiState.asStateFlow()
    private val _eventChannel = Channel<RepoListEvent>()

    val events: Flow<RepoListEvent> = _eventChannel.receiveAsFlow()

    // Drives paging from uiState.
    @OptIn(ExperimentalCoroutinesApi::class)
    val repoPagingData: Flow<PagingData<Repo>> = _uiState
        .map { state -> (state as? RepoListUiState.Success)?.sortOrder ?: SortOrder.DEFAULT }
        .distinctUntilChanged()
        .flatMapLatest { order -> getRepositoriesUseCase(order) }
        .cachedIn(viewModelScope)

    fun markReady() {
        _uiState.update { current ->
            if (current is RepoListUiState.Loading) RepoListUiState.Success() else current
        }
    }

    private fun setSortOrder(order: SortOrder) {
        _uiState.update { current ->
            (current as? RepoListUiState.Success)?.copy(sortOrder = order)
                ?: RepoListUiState.Success(sortOrder = order)
        }
    }

    private fun toggleGroupByLanguage() {
        _uiState.update { current ->
            val next = (current as? RepoListUiState.Success)?.groupByLanguage?.not() ?: true
            (current as? RepoListUiState.Success)?.copy(groupByLanguage = next)
                ?: RepoListUiState.Success(groupByLanguage = next)
        }
    }

    private fun toggleBookmark(repo: Repo) {
        viewModelScope.launch {
            toggleBookmarkUseCase(repo)
        }
    }

    fun fetchRepoDetails(repo: Repo) {
        viewModelScope.launch {
            when (val result = fetchRepoDetailsUseCase(repo)) {
                is Result.Success -> Unit
                is Result.Error   -> _eventChannel.send(RepoListEvent.ShowDetailError(result.error))
            }
        }
    }

    fun onAction(action: RepoListAction) {
        when (action) {
            is RepoListAction.SetSortOrder -> setSortOrder(action.sortOrder)
            is RepoListAction.ToggleGroupByLanguage -> toggleGroupByLanguage()
            is RepoListAction.ToggleBookmark -> toggleBookmark(action.repo)
        }
    }
}
