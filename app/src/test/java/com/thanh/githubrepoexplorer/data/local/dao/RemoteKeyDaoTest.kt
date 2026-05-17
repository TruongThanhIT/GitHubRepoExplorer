package com.thanh.githubrepoexplorer.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.data.local.db.GithubDatabase
import com.thanh.githubrepoexplorer.data.local.entity.RemoteKey
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RemoteKeyDaoTest {

    private lateinit var database: GithubDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GithubDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        remoteKeyDao = database.remoteKeyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertKey and getRemoteKey should work correctly`() = runTest {
        // Given
        val remoteKey = RemoteKey(nextUrl = "next_url")

        // When
        remoteKeyDao.insertKey(remoteKey)
        val result = remoteKeyDao.getRemoteKey()

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.nextUrl).isEqualTo("next_url")
    }

    @Test
    fun `clearRemoteKeys should clear all keys`() = runTest {
        // Given
        val remoteKey = RemoteKey(nextUrl = "next_url")
        remoteKeyDao.insertKey(remoteKey)

        // When
        remoteKeyDao.clearRemoteKeys()
        val result = remoteKeyDao.getRemoteKey()

        // Then
        assertThat(result).isNull()
    }
}
