@file:OptIn(KoinExperimentalAPI::class)

package com.jayvijay.gitpeek.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jayvijay.gitpeek.presentation.feature.searchuser.SearchUserRoute
import com.jayvijay.gitpeek.presentation.navigation.base.AppNavigator
import com.jayvijay.gitpeek.ui.theme.GitPeekTheme
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.getEntryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

class MainActivity :
    ComponentActivity(),
    AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    private val navigator: AppNavigator by inject {
        parametersOf(SearchUserRoute)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitPeekTheme {
                NavDisplay(
                    modifier = Modifier,
                    backStack = navigator.backStack,
                    onBack = { navigator.navigateBack() },
                    entryProvider = getEntryProvider(),
                    entryDecorators =
                        listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                    },
                )
            }
        }
    }
}
