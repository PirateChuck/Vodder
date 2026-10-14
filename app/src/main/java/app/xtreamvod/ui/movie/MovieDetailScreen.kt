package app.xtreamvod.ui.movie

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import app.xtreamvod.data.xtream.VodInfoResponse
import app.xtreamvod.playback.VlcLauncher
import app.xtreamvod.ui.components.ErrorState
import app.xtreamvod.ui.components.LoadingState
import app.xtreamvod.ui.components.TvPrimaryButton
import app.xtreamvod.ui.components.TvScreenScaffold
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(
    vodId: Int,
    repository: XtreamRepository,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<VodInfoResponse?>(null) }

    LaunchedEffect(vodId) {
        loading = true
        error = null
        try {
            info = repository.fetchVodInfo(vodId)
            loading = false
        } catch (e: Exception) {
            error = e.message
            loading = false
        }
    }

    TvScreenScaffold(title = info?.info?.name ?: info?.movieData?.name ?: "Movie") {
        when {
            loading -> LoadingState()
            error != null -> ErrorState(error!!)
            else -> {
                val detail = info!!
                val title = detail.info?.name ?: detail.movieData?.name ?: "Movie"
                val plot = detail.info?.plot ?: ""
                val poster = detail.info?.movieImage ?: detail.movieData?.streamIcon
                val extension = detail.movieData?.containerExtension
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    AsyncImage(
                        model = poster,
                        contentDescription = title,
                        modifier = Modifier
                            .width(240.dp)
                            .aspectRatio(2f / 3f),
                        contentScale = ContentScale.Crop,
                    )
                    androidx.compose.foundation.layout.Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(plot)
                        TvPrimaryButton(
                            text = "Play in VLC",
                            onClick = {
                                scope.launch {
                                    val url = repository.moviePlayUrl(vodId, extension)
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
