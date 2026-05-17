package com.thanh.githubrepoexplorer.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity

@Dao
interface RepoDao {
    @Query("SELECT * FROM repositories")
    fun getRepositoriesPaging(): PagingSource<Int, RepoEntity>

    @Query("SELECT * FROM repositories ORDER BY stars DESC")
    fun getReposByStars(): PagingSource<Int, RepoEntity>

    @Upsert
    suspend fun upsertAll(repos: List<RepoEntity>)

    @Query("UPDATE repositories SET isBookmarked = :isBookmarked WHERE id = :repoId")
    suspend fun updateBookmark(repoId: Long, isBookmarked: Boolean)

    @Query("DELETE FROM repositories WHERE isBookmarked = 0")
    suspend fun clearAllNonBookmarked()

    @Query("SELECT id FROM repositories WHERE isBookmarked = 1")
    suspend fun getBookmarkedIds(): List<Long>

    @Query("SELECT id FROM repositories WHERE detailsLoaded = 1")
    suspend fun getEnrichedIds(): List<Long>

    @Query("SELECT * FROM repositories WHERE detailsLoaded = 1")
    suspend fun getEnrichedEntities(): List<RepoEntity>

    @Query("SELECT detailsLoaded FROM repositories WHERE id = :repoId")
    suspend fun isDetailLoaded(repoId: Long): Boolean

    @Query(
        """
        UPDATE repositories
        SET    stars         = :stars,
               language      = :language,
               detailsLoaded = 1
        WHERE  id = :repoId
    """
    )
    suspend fun updateRepoDetails(
        repoId: Long,
        stars: Int,
        language: String?
    )
}


