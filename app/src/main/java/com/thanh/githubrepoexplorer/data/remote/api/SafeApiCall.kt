package com.thanh.githubrepoexplorer.data.remote.api

import com.thanh.githubrepoexplorer.data.mapper.toDomainError
import com.thanh.githubrepoexplorer.data.remote.model.RemoteResponse
import com.thanh.githubrepoexplorer.domain.model.error.DataError
import com.thanh.githubrepoexplorer.domain.model.Result
import retrofit2.Response

suspend fun <T> safeApiCall(
    call: suspend () -> Response<T>
): Result<T, DataError.Network> {
    return try {
        val response = call()
        if (response.isRateLimited()) {
            return Result.Error(DataError.Network.RateLimit)
        }
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(DataError.Network.Unknown)
            }
        } else {
            Result.Error(DataError.Network.HttpError)
        }
    } catch (e: Exception) {
        Result.Error(e.toDomainError())
    }
}

suspend fun <T> safeApiCallWithPagination(
    call: suspend () -> Response<T>
): Result<RemoteResponse<T>, DataError.Network> {
    return try {
        val response = call()
        if (response.isRateLimited()) {
            return Result.Error(DataError.Network.RateLimit)
        }
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val nextUrl = LinkHeaderParser.parseNextUrl(response.headers()["Link"])
                Result.Success(RemoteResponse(body, nextUrl))
            } else {
                Result.Error(DataError.Network.Unknown)
            }
        } else {
            Result.Error(DataError.Network.HttpError)
        }
    } catch (e: Exception) {
        Result.Error(e.toDomainError())
    }
}