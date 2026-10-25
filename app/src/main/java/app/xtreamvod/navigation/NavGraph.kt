package app.xtreamvod.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.xtreamvod.data.XtreamRepository
import app.xtreamvod.ui.categories.CategoryListScreen
import app.xtreamvod.ui.grid.TitleGridScreen
import app.xtreamvod.ui.main.MainScreen
import app.xtreamvod.ui.movie.MovieDetailScreen
import app.xtreamvod.ui.series.SeriesDetailScreen
import app.xtreamvod.ui.settings.SettingsScreen
import app.xtreamvod.ui.setup.SetupScreen

@Composable
fun XtreamVodNavHost(repository: XtreamRepository) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val creds = repository.getStoredCredentials()
        startDestination = if (creds != null) Routes.MAIN else Routes.SETUP
    }

    val start = startDestination
    if (start == null) {
        return
    }

    NavHost(navController = navController, startDestination = start) {
        composable(Routes.SETUP) {
            SetupScreen(
                repository = repository,
                onConnected = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SETUP) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.MAIN) {
            MainScreen(
                onMovies = { navController.navigate(Routes.categories(ContentKind.MOVIES)) },
                onSeries = { navController.navigate(Routes.categories(ContentKind.SERIES)) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                repository = repository,
                onSignedOut = {
                    navController.navigate(Routes.SETUP) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = Routes.CATEGORIES,
            arguments = listOf(
                navArgument("kind") { type = NavType.StringType },
                navArgument("parentId") { type = NavType.StringType },
                navArgument("folderName") { type = NavType.StringType },
            ),
        ) { entry ->
            val kind = entry.arguments?.getString("kind") ?: ContentKind.MOVIES
            val parentId = entry.arguments?.getString("parentId") ?: "0"
            val folderName = entry.arguments?.getString("folderName") ?: ""
            CategoryListScreen(
                kind = kind,
                parentId = parentId,
                folderName = folderName,
                repository = repository,
                onOpenFolder = { category ->
                    navController.navigate(
                        Routes.categories(
                            kind = kind,
                            parentId = category.categoryId,
                            folderName = category.categoryName,
                        ),
                    )
                },
                onOpenTitles = { category ->
                    navController.navigate(
                        Routes.grid(kind, category.categoryId, category.categoryName),
                    )
                },
            )
        }
        composable(
            route = Routes.GRID,
            arguments = listOf(
                navArgument("kind") { type = NavType.StringType },
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryName") { type = NavType.StringType },
            ),
        ) { entry ->
            val kind = entry.arguments?.getString("kind") ?: ContentKind.MOVIES
            val categoryId = entry.arguments?.getString("categoryId") ?: ""
            val categoryName = entry.arguments?.getString("categoryName") ?: ""
            TitleGridScreen(
                kind = kind,
                categoryId = categoryId,
                categoryName = categoryName,
                repository = repository,
                onMovieSelected = { navController.navigate(Routes.movie(it)) },
                onSeriesSelected = { navController.navigate(Routes.series(it)) },
            )
        }
        composable(
            route = Routes.MOVIE_DETAIL,
            arguments = listOf(navArgument("vodId") { type = NavType.IntType }),
        ) { entry ->
            val vodId = entry.arguments?.getInt("vodId") ?: return@composable
            MovieDetailScreen(vodId = vodId, repository = repository)
        }
        composable(
            route = Routes.SERIES_DETAIL,
            arguments = listOf(navArgument("seriesId") { type = NavType.IntType }),
        ) { entry ->
            val seriesId = entry.arguments?.getInt("seriesId") ?: return@composable
            SeriesDetailScreen(seriesId = seriesId, repository = repository)
        }
    }
}
