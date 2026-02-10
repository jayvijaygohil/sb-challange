package com.jayvijay.gitpeek.presentation.navigation.base

interface AppNavigator {
    val backStack: List<AppRoute>

    fun navigateTo(route: AppRoute)

    fun navigateBack(): Boolean

    fun popUpTo(
        inclusive: Boolean = false,
        predicate: (AppRoute) -> Boolean,
    )

    fun resetTo(route: AppRoute)

    fun replaceCurrent(route: AppRoute)
}
