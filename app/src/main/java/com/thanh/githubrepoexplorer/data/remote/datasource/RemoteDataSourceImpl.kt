package com.thanh.githubrepoexplorer.data.remote.datasource

import com.thanh.githubrepoexplorer.data.remote.api.GithubApi
import com.thanh.githubrepoexplorer.data.remote.api.safeApiCall
import com.thanh.githubrepoexplorer.data.remote.api.safeApiCallWithPagination
import com.thanh.githubrepoexplorer.data.remote.model.RemoteResponse
import com.thanh.githubrepoexplorer.data.remote.model.RepoDto
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.Result
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val api: GithubApi
) : RemoteDataSource {

    override suspend fun getRepositories(since: Long?): Result<RemoteResponse<List<RepoDto>>, DataError.Network> =
        safeApiCallWithPagination { api.getRepositories(since) }

    override suspend fun getRepositoriesByUrl(url: String): Result<RemoteResponse<List<RepoDto>>, DataError.Network> =
        safeApiCallWithPagination { api.getRepositoriesByUrl(url) }

    override suspend fun getRepositoryDetails(owner: String, repoName: String): Result<RepoDto, DataError.Network> =
        safeApiCall { api.getRepositoryDetails(owner, repoName) }
}