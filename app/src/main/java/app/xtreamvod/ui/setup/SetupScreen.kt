package app.xtreamvod.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xtreamvod.data.XtreamCredentials
import app.xtreamvod.data.XtreamRepository
import app.xtreamvod.ui.components.TvPrimaryButton
import app.xtreamvod.ui.components.TvScreenScaffold
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(
    repository: XtreamRepository,
    onConnected: () -> Unit,
) {
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    TvScreenScaffold(title = "Connect to Xtream") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Enter your provider server URL, username, and password.")
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = { Text("Server URL") },
                placeholder = { Text("http://example.com:8080") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )
            if (error != null) {
                Text(error!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
            TvPrimaryButton(
                text = if (loading) "Connecting…" else "Connect",
                onClick = {
                    if (loading) return@TvPrimaryButton
                    scope.launch {
                        loading = true
                        error = null
                        val result = repository.validateAndSave(
                            XtreamCredentials(serverUrl, username, password),
                        )
                        loading = false
                        result.fold(
                            onSuccess = { onConnected() },
                            onFailure = { error = it.message ?: "Connection failed" },
                        )
                    }
                },
            )
        }
    }
}
