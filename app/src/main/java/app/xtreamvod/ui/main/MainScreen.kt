package app.xtreamvod.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.xtreamvod.navigation.ContentKind
import app.xtreamvod.ui.components.TvFocusableCard
import app.xtreamvod.ui.components.TvScreenScaffold

@Composable
fun MainScreen(
    onMovies: () -> Unit,
    onSeries: () -> Unit,
    onSettings: () -> Unit,
) {
    TvScreenScaffold(title = "Xtream VOD") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TvFocusableCard(
                title = "Movies",
                subtitle = "Browse movie categories",
                onClick = onMovies,
            )
            TvFocusableCard(
                title = "Series",
                subtitle = "Browse series categories",
                onClick = onSeries,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TvFocusableCard(
                    title = "Settings",
                    subtitle = "Account and sign out",
                    onClick = onSettings,
                    modifier = Modifier.fillMaxWidth(0.4f),
                )
            }
        }
    }
}

fun contentKindTitle(kind: String): String = when (kind) {
    ContentKind.MOVIES -> "Movies"
    ContentKind.SERIES -> "Series"
    else -> kind
}
