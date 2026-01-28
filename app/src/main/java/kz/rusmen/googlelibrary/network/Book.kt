package kz.rusmen.googlelibrary.network

import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    val items: List<Book>? = emptyList(),
    val totalItems: Int = 0
)

@Serializable
data class Book(
    val id: String,
    val volumeInfo: VolumeInfo? = null,
    val saleInfo: SaleInfo? = null
)

@Serializable
data class VolumeInfo(
    val title: String = "",
    val authors: List<String> = emptyList(),
    val publisher: String = "",
    val publishedDate: String = "",
    val description: String = "",
    val imageLinks: ImageLinks? = null,
    val language: String = ""
)

@Serializable
data class ImageLinks(
    val thumbnail: String? = null
)

@Serializable
data class SaleInfo(
    val country: String = "",
    val listPrice: Price? = null,
    val retailPrice: Price? = null,
    val buyLink: String = ""
)

@Serializable
data class Price(
    val amount: Double = 0.0,
    val currencyCode: String = ""
)
