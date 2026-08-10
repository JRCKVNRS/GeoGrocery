package com.conspect.geogrocery.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.conspect.geogrocery.ui.navigation.GeoGroceryNavHost
import com.conspect.geogrocery.ui.permissions.PermissionGate
import com.conspect.geogrocery.ui.theme.GeoGroceryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Force light system-bar icons to match the app's dark canvas.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            GeoGroceryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Requests the location/notification permissions geofencing needs, then
                    // hosts the app once at least foreground permission is available.
                    PermissionGate {
                        GeoGroceryNavHost()
                    }
                }
            }
        }
    }
}
