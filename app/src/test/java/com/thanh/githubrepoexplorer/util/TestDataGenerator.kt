package com.thanh.githubrepoexplorer.util

import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity
import com.thanh.githubrepoexplorer.data.remote.model.OwnerDto
import com.thanh.githubrepoexplorer.data.remote.model.RepoDto
import com.thanh.githubrepoexplorer.domain.model.Repo

object TestDataGenerator {

    fun createRepoDto(
        id: Long = 1L,
        owner: String = "owner",
        name: String = "name"
    ) = RepoDto(
        id = id,
        name = name,
        fullName = "$owner/$name",
        description = "desc",
        owner = OwnerDto(login = owner, avatarUrl = "avatar"),
        htmlUrl = "url",
        stargazersCount = 100,
        language = "Kotlin"
    )

    fun createRepoEntity(
        id: Long = 1L,
        detailsLoaded: Boolean = false,
        isBookmarked: Boolean = false,
        stars: Int = 100,
        language: String? = "Kotlin"
    ) = RepoEntity(
        id = id,
        name = "name$id",
        fullName = "fullName$id",
        description = "desc$id",
        ownerLogin = "owner$id",
        ownerAvatarUrl = "avatar$id",
        detailsLoaded = detailsLoaded,
        isBookmarked = isBookmarked,
        stars = stars,
        language = language
    )

    fun createRepo(
        id: Long = 1L,
        name: String = "name",
        isBookmarked: Boolean = false
    ) = Repo(
        id = id,
        name = name,
        fullName = "owner/$name",
        description = "description",
        ownerLogin = "owner",
        avatarUrl = "avatar",
        stars = 100,
        language = "Kotlin",
        detailsLoaded = false,
        isBookmarked = isBookmarked
    )
}
