package com.thanh.githubrepoexplorer.data.repository

import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.data.local.datasource.LocalDataSource
import com.thanh.githubrepoexplorer.data.remote.datasource.RemoteDataSource
import com.thanh.githubrepoexplorer.data.remote.model.OwnerDto
import com.thanh.githubrepoexplorer.util.TestDataGenerator.createRepoDto
import com.thanh.githubrepoexplorer.domain.model.Result
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GithubRepositoryImplTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var underTest: GithubRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any()) } returns 0

        remoteDataSource = mockk()
        localDataSource = mockk()
        underTest = GithubRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `toggleBookmark should delegate to localDataSource`() = runTest {
        // Given
        val repoId = 1L
        val isBookmarked = true
        coEvery { localDataSource.updateBookmark(repoId, isBookmarked) } returns Unit

        // When
        underTest.toggleBookmark(repoId, isBookmarked)

        // Then
        coVerify(exactly = 1) { localDataSource.updateBookmark(repoId, isBookmarked) }
    }

    @Test
    fun `fetchRepoDetails should return success if details already loaded`() = runTest {
        // Given
        val repoId = 1L
        coEvery { localDataSource.isDetailLoaded(repoId) } returns true

        // When
        val result = underTest.fetchRepoDetails(repoId, "owner", "name")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify(exactly = 0) { remoteDataSource.getRepositoryDetails(any(), any()) }
    }

    @Test
    fun `fetchRepoDetails should fetch from remote and update local on success`() = runTest {
        // Given
        val repoId = 1L
        val owner = "owner"
        val name = "name"
        val repoDto = createRepoDto(repoId, owner, name)

        coEvery { localDataSource.isDetailLoaded(repoId) } returns false
        coEvery { remoteDataSource.getRepositoryDetails(owner, name) } returns Result.Success(repoDto)
        coEvery { localDataSource.updateRepoDetails(repoId, repoDto.stargazersCount!!, repoDto.language) } returns Unit

        // When
        val result = underTest.fetchRepoDetails(repoId, owner, name)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify(exactly = 1) { remoteDataSource.getRepositoryDetails(owner, name) }
        coVerify(exactly = 1) { localDataSource.updateRepoDetails(repoId, repoDto.stargazersCount!!, repoDto.language) }
    }

    @Test
    fun `fetchRepoDetails should return error on rate limit`() = runTest {
        // Given
        val repoId = 1L
        val owner = "owner"
        val name = "name"
        
        coEvery { localDataSource.isDetailLoaded(repoId) } returns false
        coEvery { remoteDataSource.getRepositoryDetails(owner, name) } returns Result.Error(DataError.Network.RateLimit)

        // When
        val result = underTest.fetchRepoDetails(repoId, owner, name)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(DataError.Network.RateLimit)
    }

    @Test
    fun `fetchRepoDetails should return success and swallow generic network errors`() = runTest {
        // Given
        val repoId = 1L
        val owner = "owner"
        val name = "name"

        coEvery { localDataSource.isDetailLoaded(repoId) } returns false
        coEvery { remoteDataSource.getRepositoryDetails(owner, name) } returns Result.Error(DataError.Network.Unknown)

        // When
        val result = underTest.fetchRepoDetails(repoId, owner, name)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
    }
}
