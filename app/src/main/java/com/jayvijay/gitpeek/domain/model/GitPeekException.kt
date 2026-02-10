package com.jayvijay.gitpeek.domain.model

open class GitPeekException(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message, cause) {
    class NetworkException(
        message: String? = null,
        cause: Throwable? = null,
    ) : GitPeekException(message, cause)

    class ServerException(
        val code: Int,
        message: String? = null,
        cause: Throwable? = null,
    ) : GitPeekException(message, cause)

    class RateLimitException(
        message: String? = null,
        cause: Throwable? = null,
    ) : GitPeekException(message, cause)

    class UserNotFoundException(
        message: String? = null,
        cause: Throwable? = null,
    ) : GitPeekException(message, cause)

    class DataParsingException(
        message: String? = null,
        cause: Throwable? = null,
    ) : GitPeekException(message, cause)

    class UnknownException(
        message: String? = null,
        cause: Throwable? = null,
    ) : GitPeekException(message, cause)
}
