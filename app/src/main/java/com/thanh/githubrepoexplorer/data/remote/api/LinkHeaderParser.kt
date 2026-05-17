package com.thanh.githubrepoexplorer.data.remote.api

object LinkHeaderParser {

    fun parseNextUrl(linkHeader: String?): String? {
        if (linkHeader.isNullOrBlank()) return null
        return linkHeader
            .split(",")
            .map { it.trim() }
            .find { segment -> segment.contains("""rel="next"""") }
            ?.substringAfter("<")
            ?.substringBefore(">")
            ?.takeIf { it.isNotBlank() }
    }
}