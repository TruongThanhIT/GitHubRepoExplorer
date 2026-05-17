package com.thanh.githubrepoexplorer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.thanh.githubrepoexplorer.presentation.model.RepoViewModel
import com.thanh.githubrepoexplorer.presentation.ui.compose.RepoListScreenRoot
import com.thanh.githubrepoexplorer.presentation.ui.theme.GitHubRepoExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitHubRepoExplorerTheme {
                val viewModel: RepoViewModel = hiltViewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RepoListScreenRoot(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}