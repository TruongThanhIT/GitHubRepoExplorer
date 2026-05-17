package com.thanh.githubrepoexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepoEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val stars: Int = 0,
    val language: String? = null,
    val detailsLoaded: Boolean = false,
    val isBookmarked: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
