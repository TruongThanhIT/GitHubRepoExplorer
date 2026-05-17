package com.thanh.githubrepoexplorer.data.mapper

import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.exception.DomainErrorException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import org.junit.Test

class DataErrorMapperTest {

    @Test
    fun `UnknownHostException should map to NoInternet`() {
        val exception = UnknownHostException()
        val result = exception.toDomainError()
        assertThat(result).isEqualTo(DataError.Network.NoInternet)
    }

    @Test
    fun `SocketTimeoutException should map to Timeout`() {
        val exception = SocketTimeoutException()
        val result = exception.toDomainError()
        assertThat(result).isEqualTo(DataError.Network.Timeout)
    }

    @Test
    fun `DomainErrorException should map to its internal error`() {
        val error = DataError.Network.RateLimit
        val exception = DomainErrorException(error)
        val result = exception.toDomainError()
        assertThat(result).isEqualTo(error)
    }

    @Test
    fun `Any other exception should map to Unknown`() {
        val exception = IOException("Some random error")
        val result = exception.toDomainError()
        assertThat(result).isEqualTo(DataError.Network.Unknown)
    }
}
