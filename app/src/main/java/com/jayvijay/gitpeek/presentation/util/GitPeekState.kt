package com.jayvijay.gitpeek.presentation.util

sealed class GitPeekState<out T> {
    data object Idle : GitPeekState<Nothing>()

    data object Loading : GitPeekState<Nothing>()

    data class Success<T>(
        val data: T,
    ) : GitPeekState<T>()

    data class Error(
        val message: UiText,
    ) : GitPeekState<Nothing>()
}
