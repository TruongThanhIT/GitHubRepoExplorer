package com.thanh.githubrepoexplorer.data.local.datasource

import androidx.paging.PagingSource
import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.data.local.dao.RemoteKeyDao
import com.thanh.githubrepoexplorer.data.local.dao.RepoDao
import com.thanh.githubrepoexplorer.data.local.db.GithubDatabase
import com.thanh.githubrepoexplorer.data.local.entity.RemoteKey
import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LocalDataSourceImplTest {

    private lateinit var database: GithubDatabase
    private lateinit var repoDao: RepoDao
    private lateinit var remoteKeyDao: RemoteKeyDao
    private lateinit var underTest: LocalDataSourceImpl

    @Before
    fun setUp() {
        database = mockk()
        repoDao = mockk()
        remoteKeyDao = mockk()
        underTest = LocalDataSourceImpl(database, repoDao, remoteKeyDao)
    }

    @Test
    fun `getRepositoriesPaging should delegate to repoDao`() {
        // Given
        val pagingSource = mockk<PagingSource<Int, RepoEntity>>()
        every { repoDao.getRepositoriesPaging() } returns pagingSource

        // When
        val result = underTest.getRepositoriesPaging()

        // Then
        assertThat(result).isEqualTo(pagingSource)
        coVerify(exactly = 1) { repoDao.getRepositoriesPaging() }
    }

    @Test
    fun `upsertAll should delegate to repoDao`() = runTest {
        // Given
        val repos = listOf(fakeRepoEntity(1))
        coEvery { repoDao.upsertAll(repos) } returns Unit

        // When
        underTest.upsertAll(repos)

        // Then
        coVerify(exactly = 1) { repoDao.upsertAll(repos) }
    }

    @Test
    fun `clearAllNonBookmarked should delegate to repoDao`() = runTest {
        // Given
        coEvery { repoDao.clearAllNonBookmarked() } returns Unit

        // When
        underTest.clearAllNonBookmarked()

        // Then
        coVerify(exactly = 1) { repoDao.clearAllNonBookmarked() }
    }

    @Test
    fun `updateBookmark should delegate to repoDao`() = runTest {
        // Given
        val repoId = 1L
        val isBookmarked = true
        coEvery { repoDao.updateBookmark(repoId, isBookmarked) } returns Unit

        // When
        underTest.updateBookmark(repoId, isBookmarked)

        // Then
        coVerify(exactly = 1) { repoDao.updateBookmark(repoId, isBookmarked) }
    }

    @Test
    fun `getRemoteKey should delegate to remoteKeyDao`() = runTest {
        // Given
        val remoteKey = RemoteKey(nextUrl = "url")
        coEvery { remoteKeyDao.getRemoteKey() } returns remoteKey

        // When
        val result = underTest.getRemoteKey()

        // Then
        assertThat(result).isEqualTo(remoteKey)
        coVerify(exactly = 1) { remoteKeyDao.getRemoteKey() }
    }

    @Test
    fun `insertRemoteKey should delegate to remoteKeyDao`() = runTest {
        // Given
        val remoteKey = RemoteKey(nextUrl = "url")
        coEvery { remoteKeyDao.insertKey(remoteKey) } returns Unit

        // When
        underTest.insertRemoteKey(remoteKey)

        // Then
        coVerify(exactly = 1) { remoteKeyDao.insertKey(remoteKey) }
    }

    @Test
    fun `getReposByStars should delegate to repoDao`() {
        // Given
        val pagingSource = mockk<PagingSource<Int, RepoEntity>>()
        every { repoDao.getReposByStars() } returns pagingSource

        // When
        val result = underTest.getReposByStars()

        // Then
        assertThat(result).isEqualTo(pagingSource)
        coVerify(exactly = 1) { repoDao.getReposByStars() }
    }

    @Test
    fun `updateRepoDetails should delegate to repoDao`() = runTest {
        // Given
        val repoId = 1L
        val stars = 100
        val language = "Kotlin"
        coEvery { repoDao.updateRepoDetails(repoId, stars, language) } returns Unit

        // When
        underTest.updateRepoDetails(repoId, stars, language)

        // Then
        coVerify(exactly = 1) { repoDao.updateRepoDetails(repoId, stars, language) }
    }

    @Test
    fun `getBookmarkedIds should delegate to repoDao`() = runTest {
        // Given
        val ids = listOf(1L, 2L)
        coEvery { repoDao.getBookmarkedIds() } returns ids

        // When
        val result = underTest.getBookmarkedIds()

        // Then
        assertThat(result).isEqualTo(ids)
        coVerify(exactly = 1) { repoDao.getBookmarkedIds() }
    }

    @Test
    fun `getEnrichedEntities should delegate to repoDao`() = runTest {
        // Given
        val entities = listOf(fakeRepoEntity(1))
        coEvery { repoDao.getEnrichedEntities() } returns entities

        // When
        val result = underTest.getEnrichedEntities()

        // Then
        assertThat(result).isEqualTo(entities)
        coVerify(exactly = 1) { repoDao.getEnrichedEntities() }
    }

    @Test
    fun `isDetailLoaded should delegate to repoDao`() = runTest {
        // Given
        val repoId = 1L
        coEvery { repoDao.isDetailLoaded(repoId) } returns true

        // When
        val result = underTest.isDetailLoaded(repoId)

        // Then
        assertThat(result).isTrue()
        coVerify(exactly = 1) { repoDao.isDetailLoaded(repoId) }
    }

    @Test
    fun `clearRemoteKeys should delegate to remoteKeyDao`() = runTest {
        // Given
        coEvery { remoteKeyDao.clearRemoteKeys() } returns Unit

        // When
        underTest.clearRemoteKeys()

        // Then
        coVerify(exactly = 1) { remoteKeyDao.clearRemoteKeys() }
    }

    private fun fakeRepoEntity(id: Long) = RepoEntity(
        id = id,
        name = "name",
        fullName = "fullName",
        description = "desc",
        ownerLogin = "owner",
        ownerAvatarUrl = "avatar"
    )
}
