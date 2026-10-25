package app.xtreamvod.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import app.xtreamvod.data.XtreamRepository
import app.xtreamvod.data.xtream.CategoryDto
import app.xtreamvod.ui.components.ErrorState
import app.xtreamvod.ui.components.LoadingState
import app.xtreamvod.ui.components.TvFocusableCard
import app.xtreamvod.ui.components.TvScreenScaffold
import app.xtreamvod.ui.main.contentKindTitle
import kotlinx.coroutines.launch

@Composable
fun CategoryListScreen(
    kind: String,
    parentId: String,
    folderName: String,
    repository: XtreamRepository,
    onOpenFolder: (CategoryDto) -> Unit,
    onOpenTitles: (CategoryDto) -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var categories by remember { mutableStateOf<List<CategoryDto>>(emptyList()) }
    var retryToken by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val screenTitle = when {
        folderName.isNotBlank() -> folderName
        else -> "${contentKindTitle(kind)} — Categories"
    }

    LaunchedEffect(kind, parentId, retryToken) {
        loading = true
        error = null
        try {
            categories = repository.categoriesAt(kind, parentId)
            loading = false
        } catch (e: Exception) {
            error = e.message
            loading = false
        }
    }

    TvScreenScaffold(title = screenTitle) {
        when {
            loading -> LoadingState()
            error != null -> ErrorState(error!!, onRetry = { retryToken++ })
            categories.isEmpty() -> ErrorState("No categories found.")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories, key = { it.categoryId }) { category ->
                    TvFocusableCard(
                        title = category.categoryName,
                        onClick = {
                            scope.launch {
                                if (repository.isCategoryFolder(kind, category)) {
                                    onOpenFolder(category)
                                } else {
                                    onOpenTitles(category)
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
