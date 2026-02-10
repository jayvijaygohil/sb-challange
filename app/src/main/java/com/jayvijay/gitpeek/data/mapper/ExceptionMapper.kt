package com.jayvijay.gitpeek.data.mapper

import com.jayvijay.gitpeek.domain.model.GitPeekException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toGitPeekException(customHttpMapping: ((code: Int) -> GitPeekException?)? = null): GitPeekException =
    when (this) {
        is GitPeekException -> {
            this
        }

        is HttpException -> {
            val code = code()
            customHttpMapping?.invoke(code) ?: when (code) {
                403, 429 -> GitPeekException.RateLimitException("Rate limit exceeded", this)
                else -> GitPeekException.ServerException(code, "Server error: $code", this)
            }
        }

        is IOException -> {
            GitPeekException.NetworkException("Network error", this)
        }

        is SerializationException -> {
            GitPeekException.DataParsingException("Data parsing error", this)
        }

        else -> {
            GitPeekException.UnknownException("Unknown error", this)
        }
    }
