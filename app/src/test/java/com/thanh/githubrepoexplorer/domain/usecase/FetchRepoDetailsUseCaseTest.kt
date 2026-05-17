package com.thanh.githubrepoexplorer.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.Result
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FetchRepoDetailsUseCaseTest {

    private lateinit var repository: GithubRepository
    private lateinit var underTest: FetchRepoDetailsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        underTest = FetchRepoDetailsUseCase(repository)
    }

    @Test
    fun `invoke should return Success when details already loaded`() = runTest {
        // Given
        val repo = Repo(
            id = 1L,
            name = "name",
            fullName = "fullName",
            description = "description",
            ownerLogin = "owner",
            avatarUrl = "avatar",
            stars = 100,
            language = "Kotlin",
            detailsLoaded = true,
            isBookmarked = false
        )

        // When
        val result = underTest(repo)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify(exactly = 0) { repository.fetchRepoDetails(any(), any(), any()) }
    }

    @Test
    fun `invoke should call repository when details not loaded`() = runTest {
        // Given
        val repo = Repo(
            id = 1L,
            name = "name",
            fullName = "fullName",
            description = "description",
            ownerLogin = "owner",
            avatarUrl = "avatar",
            stars = 100,
            language = "Kotlin",
            detailsLoaded = false,
            isBookmarked = false
        )
        coEvery { repository.fetchRepoDetails(repo.id, repo.ownerLogin, repo.name) } returns Result.Success(Unit)

        // When
        val result = underTest(repo)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify(exactly = 1) { repository.fetchRepoDetails(repo.id, repo.ownerLogin, repo.name) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val repo = Repo(
            id = 1L,
            name = "name",
            fullName = "fullName",
            description = "description",
            ownerLogin = "owner",
            avatarUrl = "avatar",
            stars = 100,
            language = "Kotlin",
            detailsLoaded = false,
            isBookmarked = false
        )
        val networkError = DataError.Network.NoInternet
        coEvery { repository.fetchRepoDetails(repo.id, repo.ownerLogin, repo.name) } returns Result.Error(networkError)

        // When
        val result = underTest(repo)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(networkError)
    }
}
