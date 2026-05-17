package com.thanh.githubrepoexplorer.presentation.ui.compose.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.presentation.ui.theme.GitHubRepoExplorerTheme
import org.junit.Rule
import org.junit.Test

class RepoItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun repoItem_displaysCorrectInformation() {
        val repo = Repo(
            id = 1,
            name = "Test Repo",
            fullName = "owner/Test Repo",
            description = "Test Description",
            ownerLogin = "owner",
            avatarUrl = "",
            stars = 100,
            language = "Kotlin",
            detailsLoaded = true,
            isBookmarked = false
        )

        composeTestRule.setContent {
            GitHubRepoExplorerTheme {
                RepoItem(
                    repo = repo,
                    onBookmarkClick = {},
                    onVisible = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Repo").assertIsDisplayed()
        composeTestRule.onNodeWithText("owner").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kotlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
    }

    @Test
    fun clickingBookmark_callsCallback() {
        var clicked = false
        val repo = Repo(
            id = 1,
            name = "Test Repo",
            fullName = "owner/Test Repo",
            description = "Test Description",
            ownerLogin = "owner",
            avatarUrl = "",
            stars = 100,
            language = "Kotlin",
            detailsLoaded = true,
            isBookmarked = false
        )

        composeTestRule.setContent {
            GitHubRepoExplorerTheme {
                RepoItem(
                    repo = repo,
                    onBookmarkClick = { clicked = true },
                    onVisible = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Bookmark").performClick()
        assert(clicked)
    }
}
