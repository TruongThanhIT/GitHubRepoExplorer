package com.thanh.githubrepoexplorer.di

import com.thanh.githubrepoexplorer.BuildConfig
import com.thanh.githubrepoexplorer.data.remote.api.GithubApi
import com.thanh.githubrepoexplorer.data.remote.api.GithubHeadersInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        return OkHttpClient.Builder()
            .addInterceptor(
                GithubHeadersInterceptor(
                    tokenProvider = { BuildConfig.GITHUB_TOKEN.takeIf { it.isNotBlank() } }
                )
            )
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideGithubApi(okHttpClient: OkHttpClient): GithubApi =
        Retrofit.Builder()
            .baseUrl(GithubApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApi::class.java)
}