package kz.rusmen.googlelibrary.data

import kz.rusmen.googlelibrary.network.BookApiService
import kz.rusmen.googlelibrary.network.BookResponse

interface BookRepository {
    suspend fun getBooks(
        query: String = "Jazz History",
        startIndex: Int = 0,
        maxResults: Int = 40
    ): BookResponse
}

class NetworkBookRepository(
    private val bookApiService: BookApiService
) : BookRepository {
    override suspend fun getBooks(
        query: String,
        startIndex: Int,
        maxResults: Int
    ): BookResponse {
        return bookApiService.getBooks(query = query, startIndex = startIndex, maxResults = maxResults)
    }
}
