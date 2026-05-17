package com.thanh.githubrepoexplorer.domain.repository

import androidx.paging.PagingData
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    fun getRepositories(sortOrder: SortOrder = SortOrder.DEFAULT): Flow<PagingData<Repo>>

    suspend fun toggleBookmark(repoId: Long, isBookmarked: Boolean)

    suspend fun fetchRepoDetails(
        repoId: Long,
        ownerLogin: String,
        repoName: String
    ): EmptyResult<DataError.Network>
}

