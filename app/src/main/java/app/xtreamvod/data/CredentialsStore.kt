package app.xtreamvod.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.credentialsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "credentials",
)

data class XtreamCredentials(
    val serverUrl: String,
    val username: String,
    val password: String,
) {
    val isComplete: Boolean =
        serverUrl.isNotBlank() && username.isNotBlank() && password.isNotBlank()
}

class CredentialsStore(private val context: Context) {
    private val keyServer = stringPreferencesKey("server_url")
    private val keyUser = stringPreferencesKey("username")
    private val keyPass = stringPreferencesKey("password")

    val credentialsFlow: Flow<XtreamCredentials?> = context.credentialsDataStore.data.map { prefs ->
        val server = prefs[keyServer] ?: return@map null
        val user = prefs[keyUser] ?: return@map null
        val pass = prefs[keyPass] ?: return@map null
        XtreamCredentials(server, user, pass).takeIf { it.isComplete }
    }

    suspend fun save(credentials: XtreamCredentials) {
        context.credentialsDataStore.edit { prefs ->
            prefs[keyServer] = credentials.serverUrl.trim()
            prefs[keyUser] = credentials.username.trim()
            prefs[keyPass] = credentials.password
        }
    }

    suspend fun clear() {
        context.credentialsDataStore.edit { it.clear() }
    }
}
