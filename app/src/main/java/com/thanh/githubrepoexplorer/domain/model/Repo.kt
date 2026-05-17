package com.thanh.githubrepoexplorer.domain.model

data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String,
    val ownerLogin: String,
    val avatarUrl: String,
    val stars: Int,
    val language: String?,
    val detailsLoaded: Boolean,
    val isBookmarked: Boolean
)
