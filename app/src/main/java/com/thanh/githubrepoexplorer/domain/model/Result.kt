package com.thanh.githubrepoexplorer.domain.model

import com.thanh.githubrepoexplorer.domain.model.error.Error

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : com.thanh.githubrepoexplorer.domain.model.error.Error>(val error: E) : Result<Nothing, E>
}

inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> = map {}

typealias EmptyResult<E> = Result<Unit, E>

