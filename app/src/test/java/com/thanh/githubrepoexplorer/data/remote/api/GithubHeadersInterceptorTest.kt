package com.thanh.githubrepoexplorer.data.remote.api

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Test

class GithubHeadersInterceptorTest {

    @Test
    fun `intercept should add required headers and authorization if token exists`() {
        // Given
        val token = "test_token"
        val interceptor = GithubHeadersInterceptor(tokenProvider = { token })
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url("https://api.github.com").build()
        val requestSlot = slot<Request>()

        every { chain.request() } returns request
        every { chain.proceed(capture(requestSlot)) } returns Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        // When
        interceptor.intercept(chain)

        // Then
        val interceptedRequest = requestSlot.captured
        assertThat(interceptedRequest.header("Accept")).isEqualTo("application/vnd.github+json")
        assertThat(interceptedRequest.header("User-Agent")).isEqualTo("GitHubRepoExplorer-Android/1.0")
        assertThat(interceptedRequest.header("X-GitHub-Api-Version")).isEqualTo("2022-11-28")
        assertThat(interceptedRequest.header("Authorization")).isEqualTo("Bearer $token")
    }

    @Test
    fun `intercept should not add authorization header if token is null`() {
        // Given
        val interceptor = GithubHeadersInterceptor(tokenProvider = { null })
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url("https://api.github.com").build()
        val requestSlot = slot<Request>()

        every { chain.request() } returns request
        every { chain.proceed(capture(requestSlot)) } returns mockk(relaxed = true)

        // When
        interceptor.intercept(chain)

        // Then
        val interceptedRequest = requestSlot.captured
        assertThat(interceptedRequest.header("Authorization")).isNull()
    }
}
