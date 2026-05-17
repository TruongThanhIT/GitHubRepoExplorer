package com.thanh.githubrepoexplorer.data.mapper

import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity
import com.thanh.githubrepoexplorer.data.remote.model.OwnerDto
import com.thanh.githubrepoexplorer.data.remote.model.RepoDto
import org.junit.Test

class RepoMappersTest {

    @Test
    fun `RepoDto toEntity should map correctly`() {
        // Given
        val dto = RepoDto(
            id = 1L,
            name = "name",
            fullName = "fullName",
            description = "description",
            owner = OwnerDto(login = "owner", avatarUrl = "avatar"),
            htmlUrl = "url",
            stargazersCount = 100,
            language = "Kotlin"
        )

        // When
        val entity = dto.toEntity()

        // Then
        assertThat(entity.id).isEqualTo(dto.id)
        assertThat(entity.name).isEqualTo(dto.name)
        assertThat(entity.fullName).isEqualTo(dto.fullName)
        assertThat(entity.description).isEqualTo(dto.description)
        assertThat(entity.ownerLogin).isEqualTo(dto.owner.login)
        assertThat(entity.ownerAvatarUrl).isEqualTo(dto.owner.avatarUrl)
        assertThat(entity.stars).isEqualTo(dto.stargazersCount)
        assertThat(entity.language).isEqualTo(dto.language)
        assertThat(entity.detailsLoaded).isFalse()
    }

    @Test
    fun `RepoDto toEntity with null description should map to empty string`() {
        // Given
        val dto = RepoDto(
            id = 1L,
            name = "name",
            fullName = "fullName",
            description = null,
            owner = OwnerDto(login = "owner", avatarUrl = "avatar"),
            htmlUrl = "url",
            stargazersCount = 0,
            language = null
        )

        // When
        val entity = dto.toEntity()

        // Then
        assertThat(entity.description).isEmpty()
    }

    @Test
    fun `RepoEntity toDomain should map correctly`() {
        // Given
        val entity = RepoEntity(
            id = 1L,
            name = "name",
            fullName = "fullName",
            description = "description",
            ownerLogin = "owner",
            ownerAvatarUrl = "avatar",
            stars = 100,
            language = "Kotlin",
            detailsLoaded = true,
            isBookmarked = true
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.name).isEqualTo(entity.name)
        assertThat(domain.fullName).isEqualTo(entity.fullName)
        assertThat(domain.description).isEqualTo(entity.description)
        assertThat(domain.ownerLogin).isEqualTo(entity.ownerLogin)
        assertThat(domain.avatarUrl).isEqualTo(entity.ownerAvatarUrl)
        assertThat(domain.stars).isEqualTo(entity.stars)
        assertThat(domain.language).isEqualTo(entity.language)
        assertThat(domain.detailsLoaded).isEqualTo(entity.detailsLoaded)
        assertThat(domain.isBookmarked).isEqualTo(entity.isBookmarked)
    }
}
