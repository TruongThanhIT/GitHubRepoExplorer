package com.thanh.githubrepoexplorer.presentation.ui.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.presentation.model.RepoListUiState
import com.thanh.githubrepoexplorer.presentation.ui.theme.GitHubRepoExplorerTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class RepoListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_showsLoadingIndicator() {
        composeTestRule.setContent {
            GitHubRepoExplorerTheme {
                RepoListScreen(
                    uiState = RepoListUiState.Loading,
                    pagingData = flowOf(PagingData.empty()),
                    snackbarHostState = SnackbarHostState(),
                    isRefreshing = false,
                    onRefresh = {},
                    onAction = {},
                    onFetchDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("RepoListScreen:Loading").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        val errorMessage = "Something went wrong"
        composeTestRule.setContent {
            GitHubRepoExplorerTheme {
                RepoListScreen(
                    uiState = RepoListUiState.Error(errorMessage),
                    pagingData = flowOf(PagingData.empty()),
                    snackbarHostState = SnackbarHostState(),
                    isRefreshing = false,
                    onRefresh = {},
                    onAction = {},
                    onFetchDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("RepoListScreen:Error").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun successState_showsList() {
        val repos = listOf(
            Repo(
                id = 1,
                name = "Repo 1",
                fullName = "owner/Repo 1",
                description = "Description 1",
                ownerLogin = "owner",
                avatarUrl = "",
                stars = 10,
                language = "Kotlin",
                detailsLoaded = true,
                isBookmarked = false
            )
        )

        composeTestRule.setContent {
            GitHubRepoExplorerTheme {
                RepoListScreen(
                    uiState = RepoListUiState.Success(
                        sortOrder = SortOrder.DEFAULT,
                        groupByLanguage = false
                    ),
                    pagingData = flowOf(PagingData.from(repos)),
                    snackbarHostState = SnackbarHostState(),
                    isRefreshing = false,
                    onRefresh = {},
                    onAction = {},
                    onFetchDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("RepoListScreen:List").assertIsDisplayed()
        composeTestRule.onNodeWithText("Repo 1").assertIsDisplayed()
    }

    @Test
    fun clickingSort_showsSortMenu() {
        composeTestRule.setContent {
            GitHubRepoExplorerTheme {
                RepoListScreen(
                    uiState = RepoListUiState.Success(),
                    pagingData = flowOf(PagingData.empty()),
                    snackbarHostState = SnackbarHostState(),
                    isRefreshing = false,
                    onRefresh = {},
                    onAction = {},
                    onFetchDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Sort").performClick()
        composeTestRule.onNodeWithText("Default").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sort by Stars ⭐").assertIsDisplayed()
    }
}
