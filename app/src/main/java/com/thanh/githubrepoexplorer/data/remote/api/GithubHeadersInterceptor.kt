package com.thanh.githubrepoexplorer.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response

class GithubHeadersInterceptor(
    private val tokenProvider: () -> String? = { null }
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "GitHubRepoExplorer-Android/1.0")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .apply {
                tokenProvider()?.takeIf { it.isNotBlank() }?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
            .build()
        return chain.proceed(request)
    }
}