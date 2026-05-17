package com.thanh.githubrepoexplorer.data.remote.model

data class RemoteResponse<out T>(
    val body: T,
    val nextUrl: String? = null
)