package com.thanh.githubrepoexplorer.presentation.mapper

import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.exception.DomainErrorException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun DataError.Network.toErrorMessage(): String = when (this) {
    DataError.Network.RateLimit -> "GitHub API rate limit exceeded."
    DataError.Network.NoInternet -> "No internet connection. Please check your network and retry."
    DataError.Network.Timeout -> "Request timed out. Please check your connection and retry."
    DataError.Network.HttpError -> "Server error. Please retry."
    DataError.Network.Unknown -> "An unexpected error occurred."
}

fun Throwable.toErrorMessage(): String = toPagingNetworkError().toErrorMessage()

private fun Throwable.toPagingNetworkError(): DataError.Network = when (this) {
    is DomainErrorException   -> error
    is UnknownHostException   -> DataError.Network.NoInternet
    is SocketTimeoutException -> DataError.Network.Timeout
    else                      -> DataError.Network.Unknown
}




