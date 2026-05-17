package com.thanh.githubrepoexplorer.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.thanh.githubrepoexplorer.data.local.datasource.LocalDataSource
import com.thanh.githubrepoexplorer.data.local.entity.RemoteKey
import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity
import com.thanh.githubrepoexplorer.data.mapper.toDomainError
import com.thanh.githubrepoexplorer.data.mapper.toEntity
import com.thanh.githubrepoexplorer.data.remote.datasource.RemoteDataSource
import com.thanh.githubrepoexplorer.data.remote.model.RepoDto
import com.thanh.githubrepoexplorer.domain.model.exception.DomainErrorException
import com.thanh.githubrepoexplorer.domain.model.Result

@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : RemoteMediator<Int, RepoEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoEntity>
    ): MediatorResult {
        return try {
            Log.d(TAG, "Load started. Type: $loadType")

            val loadUrl: String? = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = localDataSource.getRemoteKey()
                    if (remoteKey?.nextUrl == null) {
                        Log.d(TAG, "APPEND: no next URL, end of pagination.")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextUrl
                }
            }

            Log.d(TAG, "Fetching: ${loadUrl ?: "initial /repositories"}")

            val remoteResult = if (loadUrl == null) {
                remoteDataSource.getRepositories()
            } else {
                remoteDataSource.getRepositoriesByUrl(loadUrl)
            }

            val remoteResponse = when (remoteResult) {
                is Result.Success -> remoteResult.data
                is Result.Error -> throw DomainErrorException(remoteResult.error)
            }

            val repos = remoteResponse.body
            val nextUrl = remoteResponse.nextUrl

            Log.d(TAG, "Received ${repos.size} repos. nextUrl=$nextUrl")

            localDataSource.withTransaction {
                val bookmarkedIds = localDataSource.getBookmarkedIds().toSet()
                val enrichedById = localDataSource.getEnrichedEntities().associateBy { it.id }

                if (loadType == LoadType.REFRESH) {
                    Log.e(TAG, "REFRESH")
                    localDataSource.clearRemoteKeys()
                    localDataSource.clearAllNonBookmarked()
                }

                val entities = mergeReposWithLocalData(
                    bookmarkedIds,
                    enrichedById,
                    repos
                )

                localDataSource.upsertAll(entities)
                localDataSource.insertRemoteKey(RemoteKey(nextUrl = nextUrl))
            }

            MediatorResult.Success(endOfPaginationReached = nextUrl == null)

        } catch (e: DomainErrorException) {
            Log.e(TAG, "Domain error in load: ${e.error}")
            MediatorResult.Error(e)
        } catch (e: Exception) {
            val networkError = e.toDomainError()
            Log.e(TAG, "Error in load → $networkError", e)
            MediatorResult.Error(DomainErrorException(networkError))
        }
    }

    private fun mergeReposWithLocalData(
        bookmarkedIds: Set<Long>,
        enrichedById: Map<Long, RepoEntity>,
        repos: List<RepoDto>
    ): List<RepoEntity> {

        return repos.map { dto ->
            val enriched = enrichedById[dto.id]
            dto.toEntity().copy(
                isBookmarked = dto.id in bookmarkedIds,
                detailsLoaded = enriched != null,
                stars = enriched?.stars ?: (dto.stargazersCount ?: 0),
                language = enriched?.language ?: dto.language
            )
        }
    }

    companion object {
        private const val TAG = "GithubRemoteMediator"
    }
}