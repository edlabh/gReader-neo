package com.greader.neo.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.greader.neo.app
import com.greader.neo.ui.home.HomeScreen
import com.greader.neo.ui.login.LoginScreen
import com.greader.neo.ui.reader.ReaderScreen
import com.greader.neo.ui.settings.SettingsScreen
import com.greader.neo.ui.subscribe.SubscribeScreen
import com.greader.neo.ui.subscriptions.SubscriptionsScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val LOGIN = "login"
    const val READER = "reader/{articleId}"
    const val SUBSCRIBE = "subscribe?query={query}"
    const val SUBSCRIPTIONS = "subscriptions"

    fun reader(articleId: Long) = "reader/$articleId"
    fun subscribe(query: String? = null) =
        if (query.isNullOrBlank()) "subscribe" else "subscribe?query=${Uri.encode(query)}"
}

@Composable
fun NeoNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Compartilhamento externo (ACTION_SEND) → tela de assinatura com busca pré-preenchida.
    LaunchedEffect(Unit) {
        context.app.pendingShare.collect { text ->
            if (!text.isNullOrBlank()) {
                navController.navigate(Routes.subscribe(text))
                context.app.pendingShare.value = null
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenArticle = { id -> navController.navigate(Routes.reader(id)) },
                onConnectFeedly = { navController.navigate(Routes.LOGIN) },
                onOpenAccount = { navController.navigate(Routes.LOGIN) },
                onOpenSubscriptions = { navController.navigate(Routes.SUBSCRIPTIONS) },
            )
        }
        composable(
            route = Routes.READER,
            arguments = listOf(navArgument("articleId") { type = NavType.LongType }),
        ) { entry ->
            ReaderScreen(
                articleId = entry.arguments?.getLong("articleId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.SUBSCRIBE,
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { entry ->
            SubscribeScreen(
                initialQuery = entry.arguments?.getString("query").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenSubscribe = { navController.navigate(Routes.subscribe()) },
                onOpenLogin = { navController.navigate(Routes.LOGIN) },
                onOpenSubscriptions = { navController.navigate(Routes.SUBSCRIPTIONS) },
            )
        }
        composable(Routes.SUBSCRIPTIONS) {
            SubscriptionsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.LOGIN) {
            LoginScreen(onBack = { navController.popBackStack() })
        }
    }
}
