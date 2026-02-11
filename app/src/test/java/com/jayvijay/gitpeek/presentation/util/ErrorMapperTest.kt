package com.jayvijay.gitpeek.presentation.util

import com.jayvijay.gitpeek.R
import com.jayvijay.gitpeek.domain.model.GitPeekException
import com.jayvijay.gitpeek.domain.model.GitPeekException.DataParsingException
import com.jayvijay.gitpeek.domain.model.GitPeekException.NetworkException
import com.jayvijay.gitpeek.domain.model.GitPeekException.RateLimitException
import com.jayvijay.gitpeek.domain.model.GitPeekException.ServerException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UnknownException
import com.jayvijay.gitpeek.domain.model.GitPeekException.UserNotFoundException
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorMapperTest {
    @Test
    fun `should map UserNotFoundException to error_user_not_found string resource`() {
        val exception = UserNotFoundException("not found")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_user_not_found), result)
    }

    @Test
    fun `should map RateLimitException to error_rate_limit string resource`() {
        val exception = RateLimitException("rate limited")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_rate_limit), result)
    }

    @Test
    fun `should map NetworkException to error_network string resource`() {
        val exception = NetworkException("offline")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_network), result)
    }

    @Test
    fun `should map ServerException to error_server string resource with code`() {
        val exception = ServerException(500, "server error")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_server, listOf(500)), result)
    }

    @Test
    fun `should map ServerException with 404 to error_server with code 404`() {
        val exception = ServerException(404, "not found")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_server, listOf(404)), result)
    }

    @Test
    fun `should map DataParsingException to error_data_parsing string resource`() {
        val exception = DataParsingException("bad json")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_data_parsing), result)
    }

    @Test
    fun `should map generic RuntimeException to error_unknown string resource`() {
        val exception = RuntimeException("something went wrong")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_unknown), result)
    }

    @Test
    fun `should map base GitPeekException to error_unknown string resource`() {
        val exception = GitPeekException("base exception")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_unknown), result)
    }

    @Test
    fun `should map UnknownException to error_unknown string resource`() {
        val exception = UnknownException("unknown")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_unknown), result)
    }

    @Test
    fun `should map IllegalStateException to error_unknown string resource`() {
        val exception = IllegalStateException("bad state")

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_unknown), result)
    }

    @Test
    fun `should map NullPointerException to error_unknown string resource`() {
        val exception = NullPointerException()

        val result = exception.toUiText()

        assertEquals(UiText.StringResource(R.string.error_unknown), result)
    }
}
