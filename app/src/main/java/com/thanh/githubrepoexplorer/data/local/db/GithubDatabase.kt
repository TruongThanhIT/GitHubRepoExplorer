package com.thanh.githubrepoexplorer.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thanh.githubrepoexplorer.data.local.dao.RemoteKeyDao
import com.thanh.githubrepoexplorer.data.local.dao.RepoDao
import com.thanh.githubrepoexplorer.data.local.entity.RemoteKey
import com.thanh.githubrepoexplorer.data.local.entity.RepoEntity

@Database(
    entities = [RepoEntity::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class GithubDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val DATABASE_NAME = "github_db"
    }
}