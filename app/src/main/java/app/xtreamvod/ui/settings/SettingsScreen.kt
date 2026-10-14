package app.xtreamvod.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import app.xtreamvod.data.XtreamRepository
import app.xtreamvod.ui.components.TvPrimaryButton
import app.xtreamvod.ui.components.TvScreenScaffold
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    repository: XtreamRepository,
    onSignedOut: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    TvScreenScaffold(title = "Settings") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Sign out removes saved server URL, username, and password from this device.")
            TvPrimaryButton(
                text = "Sign out",
                onClick = {
                    scope.launch {
                        repository.clearCredentials()
                        onSignedOut()
                    }
                },
            )
        }
    }
}
