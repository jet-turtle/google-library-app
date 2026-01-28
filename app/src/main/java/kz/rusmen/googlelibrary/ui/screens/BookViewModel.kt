package kz.rusmen.googlelibrary.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.rusmen.googlelibrary.BooksApplication
import kz.rusmen.googlelibrary.data.BookRepository
import kz.rusmen.googlelibrary.network.Book
import retrofit2.HttpException
import java.io.IOException

class BookViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSelectedBook(book: Book?) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    init {
        getBooks("jazz history")
    }

    fun getBooks(query: String, isNewSearch: Boolean = true) {
        viewModelScope.launch {
            if (isNewSearch) {
                _uiState.update {
                    it.copy(
                        loadState = BookLoadState.Loading,
                        currentBooks = emptyList(),
                        selectedBook = null,
                        nextIndex = 0
                    )
                }
            }
            try {
                val response = bookRepository.getBooks(query = query, startIndex = _uiState.value.nextIndex)
                val newBooks = response.items ?: emptyList()
                if (newBooks.isEmpty()) {
                    _uiState.update { it.copy(totalItems = it.currentBooks.size) }
                    return@launch
                }

                _uiState.update { state ->
                    val updatedList = state.currentBooks + newBooks
                    state.copy(
                        currentBooks = updatedList,
                        nextIndex = state.nextIndex + newBooks.size,
                        totalItems = response.totalItems,
                        loadState = BookLoadState.Success(updatedList)
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(loadState = BookLoadState.Error) }
            } catch (e: HttpException) {
                _uiState.update { it.copy(loadState = BookLoadState.Error) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loadState = BookLoadState.Error) }
            }
        }
    }

    fun resetGridState() {
        viewModelScope.launch {
            _uiState.value.gridState.scrollToItem(0)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BooksApplication)
                val bookRepository = application.container.bookRepository
                BookViewModel(bookRepository = bookRepository)
            }
        }
    }
}
