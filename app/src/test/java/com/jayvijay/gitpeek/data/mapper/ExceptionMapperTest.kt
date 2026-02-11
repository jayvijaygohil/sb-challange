package com.jayvijay.gitpeek.data.mapper

import com.jayvijay.gitpeek.domain.model.GitPeekException
import com.jayvijay.gitpeek.domain.model.GitPeekException.DataParsingException
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.RateLimitException
import com.jayvijay.gitpeek.domain.model.GitPeekException.ServerException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UnknownException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UserNotFoundException
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ExceptionMapperTest {
    @Test
    fun `should return same instance when throwable is already a GitPeekException`() {
        val original = NetworkException("already mapped")

        val result = original.toGitPeekException()

        assertSame(original, result)
    }

    @Test
    fun `should return same instance when throwable is UserNotFoundException`() {
        val original = UserNotFoundException("not found")

        val result = original.toGitPeekException()

        assertSame(original, result)
    }

    @Test
    fun `should return same instance when throwable is ServerException`() {
        val original = ServerException(500, "server error")

        val result = original.toGitPeekException()

        assertSame(original, result)
    }

    @Test
    fun `should map 403 HttpException to RateLimitException`() {
        val httpException = createHttpException(403)

        val result = httpException.toGitPeekException()

        assertTrue(result is RateLimitException)
        assertEquals("Rate limit exceeded", result.message)
        assertSame(httpException, result.cause)
    }

    @Test
    fun `should map 429 HttpException to RateLimitException`() {
        val httpException = createHttpException(429)

        val result = httpException.toGitPeekException()

        assertTrue(result is RateLimitException)
        assertEquals("Rate limit exceeded", result.message)
    }

    @Test
    fun `should map 404 HttpException to ServerException when no custom mapping`() {
        val httpException = createHttpException(404)

        val result = httpException.toGitPeekException()

        assertTrue(result is ServerException)
        assertEquals(404, (result as ServerException).code)
        assertEquals("Server error: 404", result.message)
    }

    @Test
    fun `should map 500 HttpException to ServerException`() {
        val httpException = createHttpException(500)

        val result = httpException.toGitPeekException()

        assertTrue(result is ServerException)
        assertEquals(500, (result as ServerException).code)
        assertEquals("Server error: 500", result.message)
        assertSame(httpException, result.cause)
    }

    @Test
    fun `should map 401 HttpException to ServerException`() {
        val httpException = createHttpException(401)

        val result = httpException.toGitPeekException()

        assertTrue(result is ServerException)
        assertEquals(401, (result as ServerException).code)
    }

    @Test
    fun `should map 503 HttpException to ServerException`() {
        val httpException = createHttpException(503)

        val result = httpException.toGitPeekException()

        assertTrue(result is ServerException)
        assertEquals(503, (result as ServerException).code)
    }

    @Test
    fun `should use custom mapping when provided and returns non-null`() {
        val httpException = createHttpException(404)
        val customMapping: (Int) -> GitPeekException? = { code ->
            if (code == 404) UserNotFoundException("User not found") else null
        }

        val result = httpException.toGitPeekException(customMapping)

        assertTrue(result is UserNotFoundException)
        assertEquals("User not found", result.message)
    }

    @Test
    fun `should fall back to default mapping when custom mapping returns null`() {
        val httpException = createHttpException(500)
        val customMapping: (Int) -> GitPeekException? = { code ->
            if (code == 404) UserNotFoundException("User not found") else null
        }

        val result = httpException.toGitPeekException(customMapping)

        assertTrue(result is ServerException)
        assertEquals(500, (result as ServerException).code)
    }

    @Test
    fun `should fall back to default for 403 when custom mapping returns null`() {
        val httpException = createHttpException(403)
        val customMapping: (Int) -> GitPeekException? = { null }

        val result = httpException.toGitPeekException(customMapping)

        assertTrue(result is RateLimitException)
    }

    @Test
    fun `should map IOException to NetworkException`() {
        val ioException = IOException("connection failed")

        val result = ioException.toGitPeekException()

        assertTrue(result is NetworkException)
        assertEquals("Network error", result.message)
        assertSame(ioException, result.cause)
    }

    @Test
    fun `should map IOException subclass to NetworkException`() {
        val ioException = java.net.UnknownHostException("no host")

        val result = ioException.toGitPeekException()

        assertTrue(result is NetworkException)
    }

    @Test
    fun `should map SocketTimeoutException to NetworkException`() {
        val exception = java.net.SocketTimeoutException("timeout")

        val result = exception.toGitPeekException()

        assertTrue(result is NetworkException)
    }

    @Test
    fun `should map SerializationException to DataParsingException`() {
        val serializationException = SerializationException("bad json")

        val result = serializationException.toGitPeekException()

        assertTrue(result is DataParsingException)
        assertEquals("Data parsing error", result.message)
        assertSame(serializationException, result.cause)
    }

    @Test
    fun `should map RuntimeException to UnknownException`() {
        val runtimeException = RuntimeException("something went wrong")

        val result = runtimeException.toGitPeekException()

        assertTrue(result is UnknownException)
        assertEquals("Unknown error", result.message)
        assertSame(runtimeException, result.cause)
    }

    @Test
    fun `should map IllegalStateException to UnknownException`() {
        val exception = IllegalStateException("bad state")

        val result = exception.toGitPeekException()

        assertTrue(result is UnknownException)
    }

    @Test
    fun `should map NullPointerException to UnknownException`() {
        val exception = NullPointerException("null")

        val result = exception.toGitPeekException()

        assertTrue(result is UnknownException)
    }

    private fun createHttpException(code: Int): HttpException {
        val response = Response.error<Any>(code, "error".toResponseBody(null))
        return HttpException(response)
    }
}
