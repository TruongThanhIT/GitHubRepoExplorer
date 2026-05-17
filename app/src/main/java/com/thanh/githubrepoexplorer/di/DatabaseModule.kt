package com.thanh.githubrepoexplorer.di

import android.content.Context
import androidx.room.Room
import com.thanh.githubrepoexplorer.data.local.dao.RemoteKeyDao
import com.thanh.githubrepoexplorer.data.local.dao.RepoDao
import com.thanh.githubrepoexplorer.data.local.db.GithubDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GithubDatabase =
        Room.databaseBuilder(
            context,
            GithubDatabase::class.java,
            GithubDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideRepoDao(database: GithubDatabase): RepoDao = database.repoDao()

    @Provides
    fun provideRemoteKeyDao(database: GithubDatabase): RemoteKeyDao = database.remoteKeyDao()
}

