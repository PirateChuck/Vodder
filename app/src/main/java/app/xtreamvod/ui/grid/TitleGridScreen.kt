package app.xtreamvod.ui.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
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
    var error by remember { mutableStateOf<String?>(null) }
    var movies by remember { mutableStateOf<List<VodStreamDto>>(emptyList()) }
    var series by remember { mutableStateOf<List<SeriesStreamDto>>(emptyList()) }
    var retryToken by remember { mutableStateOf(0) }

    LaunchedEffect(kind, categoryId, retryToken) {
        loading = true
        error = null
        try {
            when (kind) {
                ContentKind.MOVIES -> {
                    movies = repository.fetchVodStreams(categoryId)
                }
                ContentKind.SERIES -> {
                    series = repository.fetchSeriesStreams(categoryId)
                }
            }
            loading = false
        } catch (e: Exception) {
            error = e.message ?: e.javaClass.simpleName
            loading = false
        }
    }

    TvScreenScaffold(title = categoryName) {
        when {
            loading -> LoadingState()
            error != null -> ErrorState(error!!, onRetry = { retryToken++ })
            kind == ContentKind.MOVIES && movies.isEmpty() -> ErrorState("No titles in this category.")
            kind == ContentKind.SERIES && series.isEmpty() -> ErrorState("No titles in this category.")
            kind == ContentKind.MOVIES -> LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(movies, key = { it.streamId }) { item ->
                    PosterCard(
                        title = item.name ?: "Movie",
                        imageUrl = item.streamIcon,
                        onClick = { onMovieSelected(item.streamId) },
                    )
                }
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(series, key = { it.seriesId }) { item ->
                    PosterCard(
                        title = item.name ?: "Series",
                        imageUrl = item.cover ?: item.streamIcon,
                        onClick = { onSeriesSelected(item.seriesId) },
                    )
                }
            }
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
