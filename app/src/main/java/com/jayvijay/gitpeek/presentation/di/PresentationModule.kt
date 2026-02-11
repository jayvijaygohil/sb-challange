@file:OptIn(KoinExperimentalAPI::class)

package com.jayvijay.gitpeek.presentation.di

import com.jayvijay.gitpeek.presentation.feature.repositorydetails.RepositoryDetailsRoute
import com.jayvijay.gitpeek.presentation.feature.repositorydetails.RepositoryDetailsScreen
import com.jayvijay.gitpeek.presentation.feature.searchuser.SearchUserRoute
import com.jayvijay.gitpeek.presentation.feature.searchuser.SearchUserScreen
import org.koin.android.annotation.ActivityRetainedScope
import org.koin.compose.navigation3.EntryProviderInstaller
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier

@Module
@Configuration
@ComponentScan("com.jayvijay.gitpeek.presentation")
object PresentationModule {
    @ActivityRetainedScope
    @Qualifier(SearchUserRoute::class)
    fun searchUserRouteEntry(): EntryProviderInstaller =
        {
            entry<SearchUserRoute> {
                SearchUserScreen()
            }
        }

    @ActivityRetainedScope
    @Qualifier(RepositoryDetailsRoute::class)
    fun repositoryDetailsRouteEntry(): EntryProviderInstaller =
        {
            entry<RepositoryDetailsRoute> { route ->
                RepositoryDetailsScreen(
                    username = route.username,
                    repositoryName = route.repositoryName,
                    totalRepoCount = route.totalRepoCount,
                )
            }
        }
}
