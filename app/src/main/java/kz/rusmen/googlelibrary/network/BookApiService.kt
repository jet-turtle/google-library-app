package kz.rusmen.googlelibrary.network

import kz.rusmen.googlelibrary.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApiService {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String = BuildConfig.BOOKS_API_KEY,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 40
    ): BookResponse
}
