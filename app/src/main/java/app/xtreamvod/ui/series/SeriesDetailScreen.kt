package app.xtreamvod.ui.series

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.xtreamvod.data.XtreamRepository
import app.xtreamvod.data.xtream.SeriesInfoResponse
import app.xtreamvod.playback.VlcLauncher
import app.xtreamvod.ui.components.ErrorState
import app.xtreamvod.ui.components.LoadingState
import app.xtreamvod.ui.components.TvFocusableCard
import app.xtreamvod.ui.components.TvScreenScaffold
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun SeriesDetailScreen(
    seriesId: Int,
    repository: XtreamRepository,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<SeriesInfoResponse?>(null) }

    LaunchedEffect(seriesId) {
        loading = true
        error = null
        try {
            info = repository.fetchSeriesInfo(seriesId)
            loading = false
        } catch (e: Exception) {
            error = e.message
            loading = false
        }
    }

    TvScreenScaffold(title = info?.info?.name ?: "Series") {
        when {
            loading -> LoadingState()
            error != null -> ErrorState(error!!)
            else -> {
                val detail = info!!
                val title = detail.info?.name ?: "Series"
                val plot = detail.info?.plot ?: ""
                val cover = detail.info?.cover
                val episodes = repository.episodesSorted(detail)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        AsyncImage(
                            model = cover,
                            contentDescription = title,
                            modifier = Modifier
                                .width(200.dp)
                                .aspectRatio(2f / 3f),
                            contentScale = ContentScale.Crop,
                        )
                        Text(plot, modifier = Modifier.fillMaxWidth())
                    }
                    if (episodes.isEmpty()) {
                        Text("No episodes listed.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(episodes, key = { (_, ep) -> ep.id }) { (season, episode) ->
                                val label = buildString {
                                    append("S")
                                    append(season)
                                    append("E")
                                    append(episode.episodeNum ?: "?")
                                    append(" — ")
                                    append(episode.title ?: "Episode")
                                }
                                TvFocusableCard(
                                    title = label,
                                    onClick = {
                                        scope.launch {
                                            val episodeId = episode.id.toIntOrNull()
                                            if (episodeId == null) {
                                                Toast.makeText(context, "Invalid episode id", Toast.LENGTH_SHORT).show()
                                                return@launch
                                            }
                                            val url = repository.episodePlayUrl(
                                                episodeId,
                                                episode.containerExtension,
                                            )
                                            if (!VlcLauncher.play(context, url)) {
                                                Toast.makeText(
                                                    context,
                                                    "VLC is not installed. Install VLC from the Amazon Appstore or sideload it.",
                                                    Toast.LENGTH_LONG,
                                                ).show()
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
