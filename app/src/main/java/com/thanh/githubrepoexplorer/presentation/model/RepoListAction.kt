package com.thanh.githubrepoexplorer.presentation.model

import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.SortOrder

sealed class RepoListAction {
    data class ToggleBookmark(val repo: Repo) : RepoListAction()
    data object ToggleGroupByLanguage : RepoListAction()
    data class SetSortOrder(val sortOrder: SortOrder) : RepoListAction()
}