package com.jayvijay.gitpeek.presentation.navigation.base

inline fun <reified T : AppRoute> AppNavigator.popUpTo(inclusive: Boolean = false) {
    popUpTo(inclusive) { it is T }
}
