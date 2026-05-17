package com.thanh.githubrepoexplorer.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class GetRepositoriesUseCaseTest {

    private lateinit var repository: GithubRepository
    private lateinit var underTest: GetRepositoriesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        underTest = GetRepositoriesUseCase(repository)
    }

    @Suppress("UnusedFlow")
    @Test
    fun `invoke should call getRepositories with correct sort order`() {
        // Given
        val sortOrder = SortOrder.STARS
        every { repository.getRepositories(sortOrder) } returns flowOf()

        // When
        val result = underTest(sortOrder)

        // Then
        assertThat(result).isNotNull()
        verify(exactly = 1) { repository.getRepositories(sortOrder) }
    }
}
