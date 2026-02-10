package com.jayvijay.gitpeek.data.di

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.jayvijay.gitpeek.BuildConfig
import com.jayvijay.gitpeek.data.network.GithubService
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

@Module
@Configuration
@ComponentScan("com.jayvijay.gitpeek.data")
object DataModule {
    private const val COIL_IMAGE_CACHE_RELATIVE_PATH = "image_cache"
    private const val COIL_MEMORY_CACHE_SIZE_PERCENTAGE = 0.25
    private const val COIL_DISK_CACHE_SIZE_PERCENTAGE = 0.02

    private const val HEADER_ACCEPT_KEY = "Accept"
    private const val HEADER_ACCEPT_VALUE = "application/vnd.github+json"
    private const val CONTENT_TYPE = "application/json"

    @Single
    fun provideCoilImageLoader(context: Context): ImageLoader =
        ImageLoader
            .Builder(context)
            .memoryCache {
                MemoryCache
                    .Builder()
                    .maxSizePercent(context, COIL_MEMORY_CACHE_SIZE_PERCENTAGE)
                    .build()
            }.diskCache {
                DiskCache
                    .Builder()
                    .directory(
                        context.cacheDir.resolve(
                            COIL_IMAGE_CACHE_RELATIVE_PATH,
                        ),
                    ).maxSizePercent(COIL_DISK_CACHE_SIZE_PERCENTAGE)
                    .build()
            }.crossfade(true)
            .build()

    @Single
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L

    @Single
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
            }
        val headerInterceptor =
            Interceptor { chain ->
                val request =
                    chain
                        .request()
                        .newBuilder()
                        .addHeader(HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE)
                        .build()
                chain.proceed(request)
            }
        return OkHttpClient
            .Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Single
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = CONTENT_TYPE.toMediaType()
        return Retrofit
            .Builder()
            .baseUrl(GithubService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Single
    fun provideGithubService(retrofit: Retrofit): GithubService = retrofit.create(GithubService::class.java)
}
