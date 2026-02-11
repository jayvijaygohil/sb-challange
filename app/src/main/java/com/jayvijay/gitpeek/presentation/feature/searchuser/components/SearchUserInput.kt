package com.jayvijay.gitpeek.presentation.feature.searchuser.components

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.jayvijay.gitpeek.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserInput(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    onSearch: () -> Unit,
) {
    SearchBarDefaults.InputField(
        modifier = Modifier,
        searchBarState = searchBarState,
        textFieldState = textFieldState,
        onSearch = { onSearch() },
        placeholder = {
            Text(
                modifier = Modifier.clearAndSetSemantics {},
                text = stringResource(R.string.search_user_hint),
            )
        },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
    )
}
