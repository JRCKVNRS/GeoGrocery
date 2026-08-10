package com.conspect.geogrocery.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.conspect.geogrocery.R
import com.conspect.geogrocery.ui.screens.lists.ListsScreen
import com.conspect.geogrocery.ui.screens.settings.SettingsScreen
import com.conspect.geogrocery.ui.util.stringRes

private enum class HomeTab { LISTS, SETTINGS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateList: () -> Unit,
    onOpenList: (String) -> Unit
) {
    var tab by remember { mutableStateOf(HomeTab.LISTS) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringRes(R.string.app_name),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = tab == HomeTab.LISTS,
                    onClick = { tab = HomeTab.LISTS },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text(stringRes(R.string.nav_lists)) },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = tab == HomeTab.SETTINGS,
                    onClick = { tab = HomeTab.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringRes(R.string.nav_settings)) },
                    colors = navItemColors()
                )
            }
        }
    ) { padding ->
        when (tab) {
            HomeTab.LISTS -> ListsScreen(
                contentPadding = padding,
                onCreateList = onCreateList,
                onOpenList = onOpenList
            )
            HomeTab.SETTINGS -> SettingsScreen(modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)
