package com.thanh.githubrepoexplorer.presentation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.thanh.githubrepoexplorer.R
import com.thanh.githubrepoexplorer.domain.model.Repo
import com.thanh.githubrepoexplorer.domain.model.SortOrder
import com.thanh.githubrepoexplorer.presentation.mapper.toUiText
import com.thanh.githubrepoexplorer.presentation.model.RepoListAction
import com.thanh.githubrepoexplorer.presentation.model.RepoListEvent
import com.thanh.githubrepoexplorer.presentation.model.RepoListUiState
import com.thanh.githubrepoexplorer.presentation.model.RepoViewModel
import com.thanh.githubrepoexplorer.presentation.ui.compose.component.AppendErrorRow
import com.thanh.githubrepoexplorer.presentation.ui.compose.component.ErrorMessage
import com.thanh.githubrepoexplorer.presentation.ui.compose.component.LanguageHeader
import com.thanh.githubrepoexplorer.presentation.ui.compose.component.RepoItem
import com.thanh.githubrepoexplorer.presentation.ui.theme.GitHubRepoExplorerTheme
import com.thanh.githubrepoexplorer.presentation.mapper.label
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun RepoListScreenRoot(
    viewModel: RepoViewModel,
    modifier: Modifier = Modifier
) {
    val repos = viewModel.repoPagingData.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isRefreshing by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        snapshotFlow { repos.loadState.refresh }
            .collect { state ->
                when (state) {
                    is LoadState.Loading -> {}
                    is LoadState.NotLoading -> {
                        isRefreshing = false
                        viewModel.markReady()
                    }

                    is LoadState.Error -> {
                        isRefreshing = false
                        viewModel.markReady()
                        snackbarHostState.showSnackbar(state.error.toUiText().asString(context))
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RepoListEvent.ShowDetailError -> snackbarHostState.showSnackbar(event.error.toUiText().asString(context))
            }
        }
    }

    RepoListScreen(
        modifier = modifier,
        uiState = uiState,
        pagingData = viewModel.repoPagingData,
        snackbarHostState = snackbarHostState,
        isRefreshing = isRefreshing,
        onRefresh = repos::refresh,
        onAction = viewModel::onAction,
        onFetchDetails = viewModel::fetchRepoDetails,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepoListScreen(
    modifier: Modifier = Modifier,
    uiState: RepoListUiState,
    pagingData: Flow<PagingData<Repo>>,
    snackbarHostState: SnackbarHostState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onAction: (RepoListAction) -> Unit,
    onFetchDetails: (Repo) -> Unit
) {
    val repos: LazyPagingItems<Repo> = pagingData.collectAsLazyPagingItems()
    var showSortMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            RepoListUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is RepoListUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.app_title),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Box {
                            IconButton(onClick = { onAction(RepoListAction.ToggleGroupByLanguage) }) {
                                Icon(
                                    imageVector = Icons.Default.GroupWork,
                                    contentDescription = stringResource(R.string.cd_group_by_language),
                                    tint = if (uiState.groupByLanguage) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = stringResource(R.string.cd_sort)
                                )
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }) {
                                SortOrder.entries.forEach { order ->
                                    DropdownMenuItem(
                                        text = { Text(order.label()) },
                                        onClick = {
                                            onAction(RepoListAction.SetSortOrder(order))
                                            showSortMenu = false
                                        },
                                        trailingIcon = if (uiState.sortOrder == order) ({
                                            Text(
                                                stringResource(R.string.sort_order_selected),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }) else null
                                    )
                                }
                            }
                        }
                    }

                    val listState = rememberLazyListState()
                    val unknownLanguageLabel = stringResource(R.string.language_unknown)
                    val groupedRepos: List<Pair<String, List<Repo>>>? by remember(uiState.groupByLanguage) {
                        derivedStateOf {
                            if (!uiState.groupByLanguage) null
                            else {
                                val snapshot = repos.itemSnapshotList.items
                                if (snapshot.isEmpty()) null
                                else snapshot
                                    .groupBy { it.language ?: unknownLanguageLabel }
                                    .toSortedMap()
                                    .map { it.key to it.value }
                            }
                        }
                    }

                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = onRefresh,
                        modifier = Modifier.weight(1f)
                    ) {
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            if (uiState.groupByLanguage && groupedRepos != null) {
                                groupedRepos!!.forEach { (language, repoList) ->
                                    item(key = "header_$language") { LanguageHeader(language) }
                                    items(items = repoList, key = { it.id }) { repo ->
                                        RepoItem(
                                            repo = repo,
                                            onBookmarkClick = {
                                                onAction(
                                                    RepoListAction.ToggleBookmark(
                                                        repo
                                                    )
                                                )
                                            },
                                            onVisible = { onFetchDetails(repo) }
                                        )
                                    }
                                }
                            } else {
                                items(
                                    count = repos.itemCount,
                                    key = repos.itemKey { it.id }) { index ->
                                    val repo = repos[index]
                                    if (repo != null) {
                                        RepoItem(
                                            repo = repo,
                                            onBookmarkClick = {
                                                onAction(
                                                    RepoListAction.ToggleBookmark(
                                                        repo
                                                    )
                                                )
                                            },
                                            onVisible = { onFetchDetails(repo) }
                                        )
                                    }
                                }
                            }

                            when (val appendState = repos.loadState.append) {
                                is LoadState.Loading -> item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .wrapContentWidth(Alignment.CenterHorizontally)
                                    )
                                }

                                is LoadState.Error -> item {
                                    AppendErrorRow(message = appendState.error.toUiText().asString())
                                }

                                else -> Unit
                            }
                        }
                    }
                }
            }

            is RepoListUiState.Error -> {
                ErrorMessage(
                    message = uiState.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data -> Snackbar(snackbarData = data) }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun PreviewLoading() {
    GitHubRepoExplorerTheme {
        RepoListScreen(
            uiState = RepoListUiState.Loading,
            pagingData = flowOf(PagingData.empty()),
            snackbarHostState = remember { SnackbarHostState() },
            isRefreshing = false,
            onRefresh = {},
            onAction = {},
            onFetchDetails = {}
        )
    }
}

@Preview(showBackground = true, name = "Success")
@Composable
private fun PreviewSuccess() {
    GitHubRepoExplorerTheme {
        RepoListScreen(
            uiState = RepoListUiState.Success(
                sortOrder = SortOrder.DEFAULT,
                groupByLanguage = false
            ),
            pagingData = flowOf(PagingData.from(fakeRepos)),
            snackbarHostState = remember { SnackbarHostState() },
            isRefreshing = false,
            onRefresh = {},
            onAction = {},
            onFetchDetails = {}
        )
    }
}

@Preview(showBackground = true, name = "Success – Grouped")
@Composable
private fun PreviewSuccessGrouped() {
    GitHubRepoExplorerTheme {
        RepoListScreen(
            uiState = RepoListUiState.Success(
                sortOrder = SortOrder.DEFAULT,
                groupByLanguage = true
            ),
            pagingData = flowOf(PagingData.from(fakeRepos)),
            snackbarHostState = remember { SnackbarHostState() },
            isRefreshing = false,
            onRefresh = {},
            onAction = {},
            onFetchDetails = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun PreviewError() {
    GitHubRepoExplorerTheme {

        RepoListScreen(
            uiState = RepoListUiState.Error("No internet connection. Please check your network and retry."),
            pagingData = flowOf(PagingData.empty()),
            snackbarHostState = remember { SnackbarHostState() },
            isRefreshing = false,
            onRefresh = {},
            onAction = {},
            onFetchDetails = {}
        )
    }
}

private fun fakeRepo(id: Long, name: String, language: String? = "Kotlin") = Repo(
    id = id,
    name = name,
    fullName = "owner/$name",
    description = "A sample repository called $name",
    ownerLogin = "owner",
    avatarUrl = "",
    stars = 1200,
    language = language,
    detailsLoaded = true,
    isBookmarked = false
)

private val fakeRepos = listOf(
    fakeRepo(1, "awesome-android", "Kotlin"),
    fakeRepo(2, "retrofit", "Java"),
    fakeRepo(3, "compose-samples", "Kotlin"),
    fakeRepo(4, "glide", "Java"),
)

