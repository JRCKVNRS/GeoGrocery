package com.conspect.geogrocery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.conspect.geogrocery.ui.screens.create.CreateListScreen
import com.conspect.geogrocery.ui.screens.detail.ListDetailScreen
import com.conspect.geogrocery.ui.screens.lists.ListsScreen

@Composable
fun GeoGroceryNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LISTS) {

        composable(Routes.LISTS) {
            ListsScreen(
                onCreateList = { navController.navigate(Routes.CREATE_LIST) },
                onOpenList = { listId -> navController.navigate(Routes.detail(listId)) }
            )
        }

        composable(Routes.CREATE_LIST) {
            CreateListScreen(
                onDone = { listId ->
                    navController.navigate(Routes.detail(listId)) {
                        popUpTo(Routes.LISTS)
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
                        // Arrived via deep link with an empty back stack: go to the list overview.
                        navController.navigate(Routes.LISTS) {
                            popUpTo(Routes.LISTS) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
