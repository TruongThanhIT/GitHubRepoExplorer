package com.thanh.githubrepoexplorer.data.mapper

import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity
import com.thanh.githubrepoexplorer.data.remote.model.RepoDto
import com.thanh.githubrepoexplorer.domain.model.Repo

fun RepoDto.toEntity(): RepoEntity {
    return RepoEntity(
        id = this.id,
        name = this.name,
        fullName = this.fullName,
        description = this.description ?: "",
        ownerLogin = this.owner.login,
        ownerAvatarUrl = this.owner.avatarUrl,
        stars = this.stargazersCount ?: 0,
        language = this.language,
        detailsLoaded = false
    )
}

fun RepoEntity.toDomain(): Repo {
    return Repo(
        id = id,
        name = name,
        fullName = fullName,
        description = description ?: "",
        ownerLogin = ownerLogin,
        avatarUrl = ownerAvatarUrl,
        stars = stars,
        language = language,
        detailsLoaded = detailsLoaded,
        isBookmarked = isBookmarked
    )
}
