package com.thanh.githubrepoexplorer.presentation.model

import com.thanh.githubrepoexplorer.domain.model.error.DataError

sealed class RepoListEvent {
    data class ShowDetailError(val error: DataError.Network) : RepoListEvent()
}