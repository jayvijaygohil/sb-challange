package com.jayvijay.gitpeek.presentation.util

import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.domain.model.GitPeekException

fun Throwable.toUiText(): UiText =
    when (this) {
        is GitPeekException.UserNotFoundException -> UiText.StringResource(R.string.error_user_not_found)
        is GitPeekException.RateLimitException -> UiText.StringResource(R.string.error_rate_limit)
        is GitPeekException.NetworkException -> UiText.StringResource(R.string.error_network)
        is GitPeekException.ServerException -> UiText.StringResource(R.string.error_server, listOf(code))
        is GitPeekException.DataParsingException -> UiText.StringResource(R.string.error_data_parsing)
        else -> UiText.StringResource(R.string.error_unknown)
    }
