package com.thanh.githubrepoexplorer.presentation.ui.compose.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.thanh.githubrepoexplorer.R

@Composable
fun RepoOwnerAvatar(url: String) {
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.AccountCircle)
    val errorPainter = rememberVectorPainter(image = Icons.Default.Warning)
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.cd_owner_avatar),
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        placeholder = placeholderPainter,
        error = errorPainter,
    )
}