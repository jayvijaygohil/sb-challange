package com.jayvijay.gitpeek

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.ksp.generated.startKoin

@KoinApplication
class GitPeekApp :
    Application(),
    SingletonImageLoader.Factory {
    private val imageLoader: ImageLoader by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@GitPeekApp)
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader
}
