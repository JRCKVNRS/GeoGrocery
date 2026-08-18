package com.conspect.geogrocery.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.conspect.geogrocery.ui.screens.create.CreateListScreen
import com.conspect.geogrocery.ui.screens.detail.ListDetailScreen
import com.conspect.geogrocery.ui.screens.home.HomeScreen
import com.conspect.geogrocery.ui.screens.quickcreate.QuickCreateScreen

@Composable
fun GeoGroceryNavHost(initialStore: String? = null) {
    val navController = rememberNavController()

    // Voice / deep-link entry ("Hey Google, maak een boodschappenlijst voor …").
    LaunchedEffect(initialStore) {
        if (!initialStore.isNullOrBlank()) {
            navController.navigate(Routes.quickCreate(Uri.encode(initialStore)))
        }
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onCreateList = { navController.navigate(Routes.CREATE_LIST) },
                onOpenList = { listId -> navController.navigate(Routes.detail(listId)) }
            )
        }

        composable(Routes.CREATE_LIST) {
            CreateListScreen(
                onDone = { listId ->
                    navController.navigate(Routes.detail(listId)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.LIST_DETAIL}/{${Routes.ARG_LIST_ID}}",
            arguments = listOf(navArgument(Routes.ARG_LIST_ID) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = Routes.DETAIL_DEEP_LINK })
        ) {
            ListDetailScreen(
                onBack = {
                    if (!navController.popBackStack()) {
                        // Arrived via deep link with an empty back stack: go to the overview.
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = "${Routes.QUICK_CREATE}/{${Routes.ARG_STORE}}",
            arguments = listOf(navArgument(Routes.ARG_STORE) { type = NavType.StringType })
        ) { backStackEntry ->
            val store = backStackEntry.arguments?.getString(Routes.ARG_STORE).orEmpty()
            QuickCreateScreen(
                store = store,
                onCreated = { listId ->
                    navController.navigate(Routes.detail(listId)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onFailed = {
                    // Couldn't geocode the spoken name: fall back to the manual create screen.
                    navController.navigate(Routes.CREATE_LIST) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
    }
}
