package com.thanh.githubrepoexplorer.domain.usecase

import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleBookmarkUseCaseTest {

    private lateinit var repository: GithubRepository
    private lateinit var underTest: ToggleBookmarkUseCase

    @Before
    fun setUp() {
        repository = mockk()
        underTest = ToggleBookmarkUseCase(repository)
    }

    @Test
    fun `invoke should call toggleBookmark with true when isBookmarked is false`() = runTest {
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
        coEvery { repository.toggleBookmark(repo.id, true) } returns Unit

        // When
        underTest(repo)

        // Then
        coVerify(exactly = 1) { repository.toggleBookmark(repo.id, true) }
    }

    @Test
    fun `invoke should call toggleBookmark with false when isBookmarked is true`() = runTest {
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
            isBookmarked = true
        )
        coEvery { repository.toggleBookmark(repo.id, false) } returns Unit

        // When
        underTest(repo)

        // Then
        coVerify(exactly = 1) { repository.toggleBookmark(repo.id, false) }
    }
}
