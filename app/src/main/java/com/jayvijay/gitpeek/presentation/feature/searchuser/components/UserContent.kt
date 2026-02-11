package com.jayvijay.gitpeek.presentation.feature.searchuser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.domain.model.Repository
import com.jayvijay.gitpeek.domain.model.User

@Composable
fun UserContent(
    user: User,
    repositories: LazyPagingItems<Repository>,
    onRepositoryClick: (repositoryName: String) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = stringResource(R.string.user_avatar_content_description),
            modifier =
                Modifier
                    .padding(top = 16.dp)
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp)),
        )

        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            items(
                count = repositories.itemCount,
                key = repositories.itemKey { it.id },
            ) { index ->
                val repo = repositories[index]
                if (repo != null) {
                    RepositoryCard(
                        repository = repo,
                        onClick = { onRepositoryClick(repo.name) },
                    )
                }
            }
        }
    }
}
