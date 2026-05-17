package com.thanh.githubrepoexplorer.data.remote.datasource

import com.thanh.githubrepoexplorer.data.remote.model.RemoteResponse
import com.thanh.githubrepoexplorer.data.remote.model.RepoDto
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.Result

interface RemoteDataSource {
    suspend fun getRepositories(since: Long? = null): Result<RemoteResponse<List<RepoDto>>, DataError.Network>
    suspend fun getRepositoriesByUrl(url: String): Result<RemoteResponse<List<RepoDto>>, DataError.Network>
    suspend fun getRepositoryDetails(owner: String, repoName: String): Result<RepoDto, DataError.Network>
}