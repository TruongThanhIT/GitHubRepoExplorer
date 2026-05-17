package com.thanh.githubrepoexplorer.data.remote.api

object GithubApiConstants {
    const val HTTP_RATE_LIMITED       = 403
    const val HTTP_TOO_MANY_REQUESTS  = 429
    const val HEADER_RATE_LIMIT_REMAINING = "x-ratelimit-remaining"
    const val HEADER_RETRY_AFTER = "retry-after"
}