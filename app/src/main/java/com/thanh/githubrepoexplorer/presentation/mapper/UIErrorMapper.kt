package com.thanh.githubrepoexplorer.presentation.mapper

import com.thanh.githubrepoexplorer.R
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.exception.DomainErrorException
import com.thanh.githubrepoexplorer.presentation.ui.util.UiText
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun DataError.Network.toUiText(): UiText = when (this) {
    DataError.Network.RateLimit -> UiText.StringResource(R.string.error_rate_limit)
    DataError.Network.NoInternet -> UiText.StringResource(R.string.error_no_internet)
    DataError.Network.Timeout -> UiText.StringResource(R.string.error_timeout)
    DataError.Network.HttpError -> UiText.StringResource(R.string.error_http)
    DataError.Network.Unknown -> UiText.StringResource(R.string.error_unknown)
}

fun Throwable.toUiText(): UiText = toPagingNetworkError().toUiText()

private fun Throwable.toPagingNetworkError(): DataError.Network = when (this) {
    is DomainErrorException   -> error
    is UnknownHostException   -> DataError.Network.NoInternet
    is SocketTimeoutException -> DataError.Network.Timeout
    else                      -> DataError.Network.Unknown
}
