package com.thanh.githubrepoexplorer.data.remote.api

import com.thanh.githubrepoexplorer.data.remote.model.RepoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GithubApi {
    @GET("repositories")
    suspend fun getRepositories(
        @Query("since") since: Long? = null
    ): Response<List<RepoDto>>

    @GET
    suspend fun getRepositoriesByUrl(
        @Url url: String
    ): Response<List<RepoDto>>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetails(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): Response<RepoDto>

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}