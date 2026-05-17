package com.thanh.githubrepoexplorer.di

import com.thanh.githubrepoexplorer.data.repository.GithubRepositoryImpl
import com.thanh.githubrepoexplorer.domain.repository.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGithubRepository(impl: GithubRepositoryImpl): GithubRepository
}