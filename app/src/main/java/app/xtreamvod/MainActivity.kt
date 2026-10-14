package app.xtreamvod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.xtreamvod.navigation.XtreamVodNavHost
import app.xtreamvod.ui.theme.XtreamVodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as XtreamVodApp
        setContent {
            XtreamVodTheme {
                XtreamVodNavHost(repository = app.repository)
            }
        }
    }
}
