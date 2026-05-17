package com.thanh.githubrepoexplorer.data.remote.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LinkHeaderParserTest {

    @Test
    fun `parseNextUrl should return url when next rel exists`() {
        val linkHeader = "<https://api.github.com/repositories?since=364>; rel=\"next\", <https://api.github.com/repositories{?since}>; rel=\"first\""
        val nextUrl = LinkHeaderParser.parseNextUrl(linkHeader)
        assertThat(nextUrl).isEqualTo("https://api.github.com/repositories?since=364")
    }

    @Test
    fun `parseNextUrl should return null when next rel is missing`() {
        val linkHeader = "<https://api.github.com/repositories?since=1>; rel=\"prev\""
        val nextUrl = LinkHeaderParser.parseNextUrl(linkHeader)
        assertThat(nextUrl).isNull()
    }

    @Test
    fun `parseNextUrl should return null when header is null or blank`() {
        assertThat(LinkHeaderParser.parseNextUrl(null)).isNull()
        assertThat(LinkHeaderParser.parseNextUrl("")).isNull()
    }
}
