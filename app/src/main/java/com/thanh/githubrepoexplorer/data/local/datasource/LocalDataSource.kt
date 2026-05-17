package com.thanh.githubrepoexplorer.data.local.datasource

import androidx.paging.PagingSource
import com.thanh.githubrepoexplorer.data.local.entity.RemoteKey
import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity

interface LocalDataSource {
    fun getRepositoriesPaging(): PagingSource<Int, RepoEntity>
    fun getReposByStars(): PagingSource<Int, RepoEntity>
    suspend fun upsertAll(repos: List<RepoEntity>)
    suspend fun clearAllNonBookmarked()
    suspend fun updateBookmark(repoId: Long, isBookmarked: Boolean)
    suspend fun updateRepoDetails(repoId: Long, stars: Int, language: String?)
    suspend fun getBookmarkedIds(): List<Long>
    suspend fun getEnrichedEntities(): List<RepoEntity>
    suspend fun isDetailLoaded(repoId: Long): Boolean
    suspend fun getRemoteKey(): RemoteKey?
    suspend fun clearRemoteKeys()
    suspend fun insertRemoteKey(remoteKey: RemoteKey)
    suspend fun <T> withTransaction(block: suspend () -> T): T
}

