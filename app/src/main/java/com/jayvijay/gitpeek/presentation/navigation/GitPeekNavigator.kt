package com.jayvijay.gitpeek.presentation.navigation

import androidx.compose.runtime.mutableStateListOf
import com.jayvijay.gitpeek.presentation.navigation.base.AppNavigator
import com.jayvijay.gitpeek.presentation.navigation.base.AppRoute
import org.koin.android.annotation.ActivityRetainedScope
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Scoped

@ActivityRetainedScope
@Scoped(binds = [AppNavigator::class])
class GitPeekNavigator(
    @InjectedParam startDestination: AppRoute,
) : AppNavigator {
    private val _backStack = mutableStateListOf(startDestination)
    override val backStack: List<AppRoute> get() = _backStack

    override fun navigateTo(route: AppRoute) {
        _backStack.add(route)
    }

    override fun navigateBack(): Boolean {
        if (_backStack.size <= 1) return false
        _backStack.removeAt(_backStack.lastIndex)
        return true
    }

    override fun popUpTo(
        inclusive: Boolean,
        predicate: (AppRoute) -> Boolean,
    ) {
        val index = _backStack.indexOfLast(predicate)
        if (index < 0) return
        val removeFrom = if (inclusive) index else index + 1
        while (_backStack.size > removeFrom) {
            _backStack.removeAt(_backStack.lastIndex)
        }
    }

    override fun resetTo(route: AppRoute) {
        _backStack.clear()
        _backStack.add(route)
    }

    override fun replaceCurrent(route: AppRoute) {
        if (_backStack.isNotEmpty()) _backStack.removeAt(_backStack.lastIndex)
        _backStack.add(route)
    }
}
