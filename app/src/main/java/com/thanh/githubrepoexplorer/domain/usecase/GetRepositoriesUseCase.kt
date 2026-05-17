package com.thanh.githubrepoexplorer.domain.usecase

import androidx.paging.PagingData
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    operator fun invoke(sortOrder: SortOrder): Flow<PagingData<Repo>> =
        repository.getRepositories(sortOrder)
}

