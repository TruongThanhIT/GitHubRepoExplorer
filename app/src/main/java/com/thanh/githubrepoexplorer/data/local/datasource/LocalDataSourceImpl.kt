package com.thanh.githubrepoexplorer.data.local.datasource

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.thanh.githubrepoexplorer.data.local.dao.RemoteKeyDao
import com.thanh.githubrepoexplorer.data.local.dao.RepoDao
import com.thanh.githubrepoexplorer.data.local.db.GithubDatabase
import com.thanh.githubrepoexplorer.data.local.entity.RemoteKey
import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val database: GithubDatabase,
    private val repoDao: RepoDao,
    private val remoteKeyDao: RemoteKeyDao
) : LocalDataSource {

    override fun getRepositoriesPaging(): PagingSource<Int, RepoEntity> =
        repoDao.getRepositoriesPaging()

    override fun getReposByStars(): PagingSource<Int, RepoEntity> =
        repoDao.getReposByStars()

    override suspend fun upsertAll(repos: List<RepoEntity>) =
        repoDao.upsertAll(repos)

    override suspend fun clearAllNonBookmarked() =
        repoDao.clearAllNonBookmarked()

    override suspend fun updateBookmark(repoId: Long, isBookmarked: Boolean) =
        repoDao.updateBookmark(repoId, isBookmarked)

    override suspend fun updateRepoDetails(repoId: Long, stars: Int, language: String?) =
        repoDao.updateRepoDetails(repoId, stars, language)

    override suspend fun getBookmarkedIds(): List<Long> =
        repoDao.getBookmarkedIds()

    override suspend fun getEnrichedEntities(): List<RepoEntity> =
        repoDao.getEnrichedEntities()

    override suspend fun isDetailLoaded(repoId: Long): Boolean =
        repoDao.isDetailLoaded(repoId)

    override suspend fun getRemoteKey(): RemoteKey? =
        remoteKeyDao.getRemoteKey()

    override suspend fun clearRemoteKeys() =
        remoteKeyDao.clearRemoteKeys()

    override suspend fun insertRemoteKey(remoteKey: RemoteKey) =
        remoteKeyDao.insertKey(remoteKey)

    override suspend fun <T> withTransaction(block: suspend () -> T): T =
        database.withTransaction(block)
}

