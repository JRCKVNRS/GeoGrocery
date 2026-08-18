package com.conspect.geogrocery.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import kotlin.math.cos

/**
 * A free OpenStreetMap map centred on [latitude]/[longitude] with a translucent circle of
 * [radiusMeters]. The circle grows/shrinks as [radiusMeters] changes; the map only re-fits its
 * zoom when the location itself changes, so sliding the radius visibly enlarges the circle.
 */
@Composable
fun LocationMap(
    latitude: Double,
    longitude: Double,
    radiusMeters: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }
    val circle = remember { Polygon() }
    val marker = remember { Marker(mapView) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                circle.fillPaint.color = AndroidColor.argb(55, 76, 175, 80)
                circle.outlinePaint.color = AndroidColor.argb(220, 76, 175, 80)
                circle.outlinePaint.strokeWidth = 5f
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                overlays.add(circle)
                overlays.add(marker)
            }
        },
        update = { map ->
            val center = GeoPoint(latitude, longitude)
            marker.position = center
            circle.points = Polygon.pointsAsCircle(center, radiusMeters.toDouble())

            // Re-fit on every change so the whole circle stays framed; as the radius grows the
            // circle covers more of the map (streets shrink), which reads as "the area grows".
            map.post {
                map.zoomToBoundingBox(boundingBox(latitude, longitude, radiusMeters), false, 48)
            }
            map.invalidate()
        }
    )
}

/** Bounding box roughly 1.4× the circle radius so the whole circle is comfortably visible. */
private fun boundingBox(lat: Double, lon: Double, radiusMeters: Float): BoundingBox {
    val latDelta = radiusMeters / 111_320.0 * 1.4
    val lonDelta = radiusMeters / (111_320.0 * cos(Math.toRadians(lat))) * 1.4
    return BoundingBox(lat + latDelta, lon + lonDelta, lat - latDelta, lon - lonDelta)
}
