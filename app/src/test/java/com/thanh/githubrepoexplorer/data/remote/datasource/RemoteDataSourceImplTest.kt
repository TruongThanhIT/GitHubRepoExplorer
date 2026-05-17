package com.thanh.githubrepoexplorer.data.remote.datasource

import com.google.common.truth.Truth.assertThat
import com.thanh.githubrepoexplorer.data.remote.api.GithubApi
import com.thanh.githubrepoexplorer.domain.model.Result
import com.thanh.githubrepoexplorer.util.TestDataGenerator.createRepoDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response

class RemoteDataSourceImplTest {

    private val api = mockk<GithubApi>()
    private val underTest = RemoteDataSourceImpl(api)

    @Test
    fun `getRepositories should return success when api call is successful`() = runTest {
        // Given
        val repos = listOf(createRepoDto(1))
        val response = Response.success(repos, Headers.headersOf("Link", "<url>; rel=\"next\""))
        coEvery { api.getRepositories(any()) } returns response

        // When
        val result = underTest.getRepositories()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val data = (result as Result.Success).data
        assertThat(data.body).isEqualTo(repos)
        assertThat(data.nextUrl).isEqualTo("url")
    }

    @Test
    fun `getRepositoryDetails should return success when api call is successful`() = runTest {
        // Given
        val repo = createRepoDto(1)
        coEvery { api.getRepositoryDetails(any(), any()) } returns Response.success(repo)

        // When
        val result = underTest.getRepositoryDetails("owner", "name")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(repo)
    }

    @Test
    fun `getRepositories should return error when api call fails`() = runTest {
        // Given
        val errorBody = "{}".toResponseBody()
        coEvery { api.getRepositories(any()) } returns Response.error(404, errorBody)

        // When
        val result = underTest.getRepositories()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}
