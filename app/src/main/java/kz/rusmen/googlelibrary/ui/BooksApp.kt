package kz.rusmen.googlelibrary.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.rusmen.googlelibrary.R
import kz.rusmen.googlelibrary.ui.screens.BookViewModel
import kz.rusmen.googlelibrary.ui.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior() // smart hiding app bar
    val viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BooksTopAppBar(
                searchQuery = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                onSearch = { viewModel.getBooks(uiState.searchQuery, isNewSearch = true) },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeScreen(
                loadState = uiState.loadState,
                totalItems = uiState.totalItems,
                retryAction = { viewModel.getBooks(uiState.searchQuery) },
                selectedBook = uiState.selectedBook,
                onBookClick = { viewModel.updateSelectedBook(it) },
                onLoadMore = { viewModel.getBooks(uiState.searchQuery, isNewSearch = false) },
                gridState = uiState.gridState,
                resetGridState = { viewModel.resetGridState() },
                contentPadding = innerPadding,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksTopAppBar(
    searchQuery: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val googleBlue = colorResource(R.color.google_blue)
    val googleRed = colorResource(R.color.google_red)
    val googleYellow = colorResource(R.color.google_yellow)
    val googleGreen = colorResource(R.color.google_green)

    val googleLogoText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = googleBlue)) { append("G") }
        withStyle(style = SpanStyle(color = googleRed)) { append("o") }
        withStyle(style = SpanStyle(color = googleYellow)) { append("o") }
        withStyle(style = SpanStyle(color = googleBlue)) { append("g") }
        withStyle(style = SpanStyle(color = googleGreen)) { append("l") }
        withStyle(style = SpanStyle(color = googleRed)) { append("e") }
    }

    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text(
                        text = googleLogoText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Library",
                        color = googleBlue,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            text = "Search books...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                onValueChange("")
                                focusRequester.requestFocus() // hoist keyboard
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearch()
                        focusManager.clearFocus() // hide keyboard after search

                    }),
                    shape = RoundedCornerShape(24.dp)
                )
            }
        },
        modifier = modifier.shadow(
            elevation = if (scrollBehavior.state.contentOffset < 0f) 8.dp else 0.dp
        )
    )
}
