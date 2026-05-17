package com.thanh.githubrepoexplorer.data.mapper

import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.exception.DomainErrorException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toDomainError(): DataError.Network = when (this) {
    is DomainErrorException   -> error
    is UnknownHostException   -> DataError.Network.NoInternet
    is SocketTimeoutException -> DataError.Network.Timeout
    else                      -> DataError.Network.Unknown
}