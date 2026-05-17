package com.thanh.githubrepoexplorer.presentation.mapper

import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.R
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.exception.DomainErrorException
import com.thanh.githubrepoexplorer.presentation.ui.util.UiText
import org.junit.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UIErrorMapperTest {

    @Test
    fun `DataError Network toUiText should map correctly`() {
        // Then
        assertThat((DataError.Network.NoInternet.toUiText() as UiText.StringResource).resId)
            .isEqualTo(R.string.error_no_internet)
        
        assertThat((DataError.Network.Timeout.toUiText() as UiText.StringResource).resId)
            .isEqualTo(R.string.error_timeout)
        
        assertThat((DataError.Network.RateLimit.toUiText() as UiText.StringResource).resId)
            .isEqualTo(R.string.error_rate_limit)
        
        assertThat((DataError.Network.HttpError.toUiText() as UiText.StringResource).resId)
            .isEqualTo(R.string.error_http)
        
        assertThat((DataError.Network.Unknown.toUiText() as UiText.StringResource).resId)
            .isEqualTo(R.string.error_unknown)
    }

    @Test
    fun `Throwable toUiText should map UnknownHostException to NoInternet`() {
        // Given
        val exception = UnknownHostException()
        
        // When
        val uiText = exception.toUiText() as UiText.StringResource
        
        // Then
        assertThat(uiText.resId).isEqualTo(R.string.error_no_internet)
    }

    @Test
    fun `Throwable toUiText should map SocketTimeoutException to Timeout`() {
        // Given
        val exception = SocketTimeoutException()
        
        // When
        val uiText = exception.toUiText() as UiText.StringResource
        
        // Then
        assertThat(uiText.resId).isEqualTo(R.string.error_timeout)
    }

    @Test
    fun `Throwable toUiText should map DomainErrorException correctly`() {
        // Given
        val error = DataError.Network.RateLimit
        val exception = DomainErrorException(error)
        
        // When
        val uiText = exception.toUiText() as UiText.StringResource
        
        // Then
        assertThat(uiText.resId).isEqualTo(R.string.error_rate_limit)
    }

    @Test
    fun `Throwable toUiText should map other exceptions to Unknown`() {
        // Given
        val exception = RuntimeException()
        
        // When
        val uiText = exception.toUiText() as UiText.StringResource
        
        // Then
        assertThat(uiText.resId).isEqualTo(R.string.error_unknown)
    }
}
