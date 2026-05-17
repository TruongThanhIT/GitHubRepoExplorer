package com.thanh.githubrepoexplorer.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.thanh.githubrepoexplorer.data.local.datasource.LocalDataSource
import com.thanh.githubrepoexplorer.data.remote.datasource.RemoteDataSource
import com.thanh.githubrepoexplorer.data.mapper.toDomain
import com.thanh.githubrepoexplorer.data.mapper.toDomainError
import com.thanh.githubrepoexplorer.data.remote.GithubRemoteMediator
import com.thanh.githubrepoexplorer.di.qualifier.IoDispatcher
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import com.thanh.githubrepoexplorer.domain.model.EmptyResult
import com.thanh.githubrepoexplorer.domain.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GithubRepository {

    private val inFlightIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())

    @OptIn(ExperimentalPagingApi::class)
    override fun getRepositories(sortOrder: SortOrder): Flow<PagingData<Repo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            remoteMediator = GithubRemoteMediator(remoteDataSource, localDataSource),
            pagingSourceFactory = {
                when (sortOrder) {
                    SortOrder.DEFAULT -> localDataSource.getRepositoriesPaging()
                    SortOrder.STARS -> localDataSource.getReposByStars()
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun toggleBookmark(repoId: Long, isBookmarked: Boolean) =
        withContext(ioDispatcher) {
            localDataSource.updateBookmark(repoId, isBookmarked)
        }

    override suspend fun fetchRepoDetails(
        repoId: Long,
        ownerLogin: String,
        repoName: String
    ): EmptyResult<DataError.Network> = withContext(ioDispatcher) {
        if (!inFlightIds.add(repoId)) return@withContext Result.Success(Unit)

        return@withContext try {
            if (localDataSource.isDetailLoaded(repoId)) return@withContext Result.Success(Unit)

            Log.d(TAG, "Enriching $ownerLogin/$repoName")

            val detail = when (val detailResult = remoteDataSource.getRepositoryDetails(ownerLogin, repoName)) {
                is Result.Success -> detailResult.data
                is Result.Error -> {
                    if (detailResult.error == DataError.Network.RateLimit) {
                        return@withContext Result.Error(DataError.Network.RateLimit)
                    }
                    Log.w(
                        TAG, "Detail fetch failed for $ownerLogin/$repoName " +
                                "(${detailResult.error}) — will retry on next scroll"
                    )
                    return@withContext Result.Success(Unit)
                }
            }

            localDataSource.updateRepoDetails(
                repoId = repoId,
                stars = detail.stargazersCount ?: 0,
                language = detail.language
            )
            Log.d(TAG, "Enriched $ownerLogin/$repoName → ⭐${detail.stargazersCount}")
            Result.Success(Unit)
        } catch (e: Exception) {
            when (val networkError = e.toDomainError()) {
                DataError.Network.RateLimit -> Result.Error(networkError)
                else -> {
                    Log.w(TAG, "Enrichment silently failed for $ownerLogin/$repoName: ${e.message}")
                    Result.Success(Unit)
                }
            }
        } finally {
            inFlightIds.remove(repoId)
        }
    }

    companion object {
        private const val TAG = "GithubRepositoryImpl"
        private const val PAGE_SIZE = 30
        private const val PREFETCH_DISTANCE = 10
    }
}