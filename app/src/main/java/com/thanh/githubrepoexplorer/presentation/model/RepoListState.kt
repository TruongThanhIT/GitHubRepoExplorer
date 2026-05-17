package com.thanh.githubrepoexplorer.presentation.model

import com.thanh.githubrepoexplorer.domain.model.SortOrder

sealed class RepoListUiState {

    data object Loading : RepoListUiState()

    data class Success(
        val sortOrder: SortOrder = SortOrder.DEFAULT,
        val groupByLanguage: Boolean = false
    ) : RepoListUiState()

    data class Error(val message: String) : RepoListUiState()
}

