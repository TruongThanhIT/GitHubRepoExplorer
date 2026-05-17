package com.thanh.githubrepoexplorer.domain.usecase

import com.thanh.githubrepoexplorer.domain.model.EmptyResult
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.Result
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import javax.inject.Inject

class FetchRepoDetailsUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(repo: Repo): EmptyResult<DataError.Network> {
        if (repo.detailsLoaded) return Result.Success(Unit)
        return repository.fetchRepoDetails(repo.id, repo.ownerLogin, repo.name)
    }
}



