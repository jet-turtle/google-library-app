package kz.rusmen.googlelibrary.ui.screens

import androidx.compose.foundation.lazy.grid.LazyGridState
import kz.rusmen.googlelibrary.network.Book

sealed interface BookLoadState {
    data object Error : BookLoadState
    data object Loading : BookLoadState
    data class Success(val books: List<Book>) : BookLoadState
}

data class BookUiState(
    val loadState: BookLoadState = BookLoadState.Loading,
    val searchQuery: String = "jazz history",
    val selectedBook: Book? = null,
    val currentBooks: List<Book> = emptyList(),
    val nextIndex: Int = 0,
    val totalItems: Int = 0,
    val gridState: LazyGridState = LazyGridState()
)
