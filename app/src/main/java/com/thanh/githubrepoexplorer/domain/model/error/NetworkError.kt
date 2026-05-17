package com.thanh.githubrepoexplorer.domain.model.error

sealed interface DataError : Error {
    enum class Network : DataError {
        NoInternet,
        Timeout,
        RateLimit,
        HttpError,
        Unknown
    }

    enum class Local : DataError {
        DiskFull,
        DBError
    }
}

