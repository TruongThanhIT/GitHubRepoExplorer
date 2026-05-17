package com.thanh.githubrepoexplorer.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.data.local.db.GithubDatabase
import com.thanh.githubrepoexplorer.util.TestDataGenerator.createRepoEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RepoDaoTest {

    private lateinit var database: GithubDatabase
    private lateinit var repoDao: RepoDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GithubDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repoDao = database.repoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `upsertAll and getEnrichedEntities should work correctly`() = runTest {
        // Given
        val repos = listOf(
            createRepoEntity(1, detailsLoaded = true),
            createRepoEntity(2, detailsLoaded = false)
        )

        // When
        repoDao.upsertAll(repos)
        val enriched = repoDao.getEnrichedEntities()

        // Then
        assertThat(enriched).hasSize(1)
        assertThat(enriched[0].id).isEqualTo(1L)
    }

    @Test
    fun `updateBookmark should update the correct repo`() = runTest {
        // Given
        val repo = createRepoEntity(1, isBookmarked = false)
        repoDao.upsertAll(listOf(repo))

        // When
        repoDao.updateBookmark(1L, true)
        val bookmarkedIds = repoDao.getBookmarkedIds()

        // Then
        assertThat(bookmarkedIds).containsExactly(1L)
    }

    @Test
    fun `clearAllNonBookmarked should keep bookmarked repos`() = runTest {
        // Given
        val repos = listOf(
            createRepoEntity(1, isBookmarked = true),
            createRepoEntity(2, isBookmarked = false)
        )
        repoDao.upsertAll(repos)

        // When
        repoDao.clearAllNonBookmarked()
        
        // Then
        val bookmarkedIds = repoDao.getBookmarkedIds()
        assertThat(bookmarkedIds).containsExactly(1L)
    }

    @Test
    fun `updateRepoDetails should update details and set detailsLoaded to true`() = runTest {
        // Given
        val repo = createRepoEntity(1, detailsLoaded = false, stars = 0)
        repoDao.upsertAll(listOf(repo))

        // When
        repoDao.updateRepoDetails(1L, 100, "Kotlin")
        val isLoaded = repoDao.isDetailLoaded(1L)
        val enriched = repoDao.getEnrichedEntities()

        // Then
        assertThat(isLoaded).isTrue()
        assertThat(enriched).hasSize(1)
        assertThat(enriched[0].stars).isEqualTo(100)
        assertThat(enriched[0].language).isEqualTo("Kotlin")
    }


}
