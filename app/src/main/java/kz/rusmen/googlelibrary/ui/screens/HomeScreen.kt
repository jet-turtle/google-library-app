package kz.rusmen.googlelibrary.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kz.rusmen.googlelibrary.R
import kz.rusmen.googlelibrary.network.Book

@Composable
fun HomeScreen(
    loadState: BookLoadState,
    totalItems: Int,
    retryAction: () -> Unit,
    selectedBook: Book?,
    onBookClick: (Book?) -> Unit,
    onLoadMore: () -> Unit,
    gridState: LazyGridState,
    resetGridState: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    if (selectedBook != null) {
        BackHandler { onBookClick(null) }
        BookDetailScreen(
            book = selectedBook,
            onDismiss = { onBookClick(null) },
            contentPadding = contentPadding
        )
    } else {
        when (loadState) {
            is BookLoadState.Error -> ErrorScreen(retryAction = retryAction)
            is BookLoadState.Loading -> {
                resetGridState()
                LoadingScreen()
            }
            is BookLoadState.Success -> BookGridScreen(
                books = loadState.books,
                totalItems = totalItems,
                onBookClick = onBookClick,
                onLoadMore = onLoadMore,
                gridState = gridState,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "loading")

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
        Image(
            modifier = Modifier
                .size(200.dp)
                .rotate(rotation),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading)
        )
    }
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier
) {
    val bookCover = book.volumeInfo?.imageLinks?.thumbnail?.replace("http://", "https://")
    val language = book.volumeInfo?.language ?: ""
    val title = book.volumeInfo?.title ?: ""
    val listPrice = book.saleInfo?.listPrice?.amount?.toString() ?: ""
    val retailPrice = book.saleInfo?.retailPrice?.amount?.toString() ?: ""
    val currency = book.saleInfo?.listPrice?.currencyCode ?: ""

    Card(
        modifier = modifier,
        shape = RectangleShape
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(bookCover)
                    .crossfade(true)
                    .placeholder(R.drawable.placeholder_scaled)
                    .error(R.drawable.ic_broken_image)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.book_cover),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.67f)
            )
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val customShadow = Shadow(
                    color = Color.DarkGray,
                    offset = Offset(4f, 4f),
                    blurRadius = 4f
                )
                Text(
                    text = language,
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(R.color.google_yellow),
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = customShadow
                    ),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(R.color.google_yellow),
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = customShadow
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "$listPrice ${if (listPrice.isNotEmpty()) currency else ""}",
                        color = colorResource(R.color.google_yellow),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.W300,
                            textDecoration = TextDecoration.LineThrough,
                            shadow = customShadow
                        )
                    )
                    Text(
                        text = "$retailPrice ${if (retailPrice.isNotEmpty()) currency else ""}",
                        color = colorResource(R.color.google_yellow),
                        style = MaterialTheme.typography.titleSmall.copy(
                            shadow = customShadow
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BookGridScreen(
    books: List<Book>,
    totalItems: Int,
    onBookClick: (Book?) -> Unit,
    onLoadMore: () -> Unit,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyVerticalGrid(
        state = gridState,
        modifier = modifier,
        columns = GridCells.Adaptive(150.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = books,
            key = { book -> "${book.id}_${books.indexOf(book)}" }
        ) { book ->
            BookCard(
                book = book,
                modifier = Modifier
                    .fillMaxWidth()
                    //.aspectRatio(0.63f)
                    .clickable { onBookClick(book) }
            )
        }
        if (books.isNotEmpty() && books.size < totalItems) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Button(
                    onClick = onLoadMore,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Load More (${books.size} / $totalItems)")
                }
            }
        }
    }
}

@Composable
fun BookDetailScreen(
    book: Book,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    val bookCover = book.volumeInfo?.imageLinks?.thumbnail?.replace("http://", "https://")
    val title = book.volumeInfo?.title ?: ""
    val authors = book.volumeInfo?.authors?.joinToString(", ") ?: ""
    val publisher = book.volumeInfo?.publisher ?: ""
    val description = book.volumeInfo?.description ?: ""
    val language = book.volumeInfo?.language ?: ""
    val price = "${book.saleInfo?.listPrice?.amount?.toString() ?: ""} ${book.saleInfo?.listPrice?.currencyCode ?: ""}"
    val buyLink = book.saleInfo?.buyLink ?: ""

    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(bookCover)
                .crossfade(true)
                .placeholder(R.drawable.placeholder_scaled)
                .error(R.drawable.ic_broken_image)
                .build(),
            contentDescription = stringResource(R.string.book_cover),
            modifier = Modifier.width(200.dp),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(8.dp))
        BookInfoRow("Title", title)
        BookInfoRow("Authors", authors)
        BookInfoRow("Publisher", publisher)
        BookInfoRow("Description", description)
        BookInfoRow("Language", language)
        BookInfoRow("Price", price)
        if (buyLink.isNotEmpty()) {
            Text(
                text = "Buy link: $buyLink",
                modifier = Modifier.clickable { uriHandler.openUri(buyLink) },
                color = Color.Blue
            )
        }
    }
}

@Composable
fun BookInfoRow(label: String, value: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$label: ")
            }
            append(value)
        },
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
