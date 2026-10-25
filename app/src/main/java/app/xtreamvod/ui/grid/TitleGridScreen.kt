package app.xtreamvod.ui.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import app.xtreamvod.data.XtreamRepository
import app.xtreamvod.data.xtream.SeriesStreamDto
import app.xtreamvod.data.xtream.VodStreamDto
import app.xtreamvod.navigation.ContentKind
import app.xtreamvod.ui.components.ErrorState
import app.xtreamvod.ui.components.LoadingState
import app.xtreamvod.ui.components.TvScreenScaffold
import coil.compose.AsyncImage

@Composable
fun TitleGridScreen(
    kind: String,
    categoryId: String,
    categoryName: String,
    repository: XtreamRepository,
    onMovieSelected: (Int) -> Unit,
    onSeriesSelected: (Int) -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var refreshingFromProvider by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var movies by remember { mutableStateOf<List<VodStreamDto>>(emptyList()) }
    var series by remember { mutableStateOf<List<SeriesStreamDto>>(emptyList()) }
    var retryToken by remember { mutableStateOf(0) }

    LaunchedEffect(kind, categoryId, retryToken) {
        loading = true
        refreshingFromProvider = false
        error = null
        movies = emptyList()
        series = emptyList()
        if (categoryId.isBlank()) {
            error = "Missing category id. Go back and try again."
            loading = false
            return@LaunchedEffect
        }
        try {
            when (kind) {
                ContentKind.MOVIES -> {
                    repository.loadVodGrid(categoryId) { items, finished ->
                        movies = items
                        loading = items.isEmpty() && !finished
                        refreshingFromProvider = !finished
                    }
                }
                ContentKind.SERIES -> {
                    repository.loadSeriesGrid(categoryId) { items, finished ->
                        series = items
                        loading = items.isEmpty() && !finished
                        refreshingFromProvider = !finished
                    }
                }
            }
        } catch (e: Exception) {
            error = e.message ?: e.javaClass.simpleName
            loading = false
            refreshingFromProvider = false
        }
    }

    TvScreenScaffold(title = categoryName) {
        when {
            error != null -> ErrorState(error!!, onRetry = { retryToken++ })
            loading -> LoadingState(
                message = "Downloading catalogue from your provider…\n" +
                    "Xtream sends a large JSON blob per category (not just names). First open can be slow.",
            )
            kind == ContentKind.MOVIES && movies.isEmpty() -> ErrorState("No titles in this category.")
            kind == ContentKind.SERIES && series.isEmpty() -> ErrorState("No titles in this category.")
            kind == ContentKind.MOVIES -> TitleGrid(
                items = movies,
                refreshing = refreshingFromProvider,
                itemTitle = { it.name ?: "Movie" },
                itemImage = { it.streamIcon },
                itemKey = { it.streamId },
                onClick = { onMovieSelected(it.streamId) },
            )
            else -> TitleGrid(
                items = series,
                refreshing = refreshingFromProvider,
                itemTitle = { it.name ?: "Series" },
                itemImage = { it.cover ?: it.streamIcon },
                itemKey = { it.seriesId },
                onClick = { onSeriesSelected(it.seriesId) },
            )
        }
    }
}

@Composable
private fun <T> TitleGrid(
    items: List<T>,
    refreshing: Boolean,
    itemTitle: (T) -> String,
    itemImage: (T) -> String?,
    itemKey: (T) -> Int,
    onClick: (T) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items, key = { itemKey(it) }) { item ->
                PosterCard(
                    title = itemTitle(item),
                    imageUrl = itemImage(item),
                    onClick = { onClick(item) },
                )
            }
        }
        if (refreshing) {
            Text(
                text = "Updating from provider…",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PosterCard(
    title: String,
    imageUrl: String?,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(),
        colors = ClickableSurfaceDefaults.colors(),
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
