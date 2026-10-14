package app.xtreamvod.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D1B2A),
    secondary = Color(0xFFBB86FC),
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    onBackground = Color(0xFFE6EDF3),
    onSurface = Color(0xFFE6EDF3),
)

@Composable
fun XtreamVodTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
