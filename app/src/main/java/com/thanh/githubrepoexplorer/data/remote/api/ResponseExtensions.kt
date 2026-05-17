package com.thanh.githubrepoexplorer.data.remote.api

import com.thanh.githubrepoexplorer.data.remote.api.GithubApiConstants.HEADER_RATE_LIMIT_REMAINING
import com.thanh.githubrepoexplorer.data.remote.api.GithubApiConstants.HEADER_RETRY_AFTER
import com.thanh.githubrepoexplorer.data.remote.api.GithubApiConstants.HTTP_RATE_LIMITED
import com.thanh.githubrepoexplorer.data.remote.api.GithubApiConstants.HTTP_TOO_MANY_REQUESTS
import retrofit2.Response

/**
 * Returns true when GitHub signals that a rate limit has been hit.
 *
 * GitHub can respond with **403 or 429** for both primary and secondary limits:
 * - Primary limit  → `x-ratelimit-remaining` == 0
 * - Secondary limit → `retry-after` header is present
 *
 * Reference: https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api
 */
fun <T> Response<T>.isRateLimited(): Boolean {
    val code = code()
    if (code != HTTP_RATE_LIMITED && code != HTTP_TOO_MANY_REQUESTS) return false
    val remaining = headers()[HEADER_RATE_LIMIT_REMAINING]?.toIntOrNull()
    val hasRetryAfter = headers()[HEADER_RETRY_AFTER] != null
    return (remaining != null && remaining == 0) || hasRetryAfter
}