package com.thanh.githubrepoexplorer.domain.usecase

import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(repo: Repo) {
        repository.toggleBookmark(repo.id, !repo.isBookmarked)
    }
}

