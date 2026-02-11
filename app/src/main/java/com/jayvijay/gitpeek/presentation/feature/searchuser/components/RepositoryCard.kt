package com.jayvijay.gitpeek.presentation.feature.searchuser.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.domain.model.Repository

@Composable
fun RepositoryCard(
    repository: Repository,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = repository.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    repository.description.ifEmpty {
                        stringResource(R.string.repo_no_description)
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
