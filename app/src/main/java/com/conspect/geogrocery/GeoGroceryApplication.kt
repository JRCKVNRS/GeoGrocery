package com.conspect.geogrocery

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration as OsmConfig
import javax.inject.Inject

@HiltAndroidApp
class GeoGroceryApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        // osmdroid requires a unique User-Agent for OSM tile requests (per OSM tile policy).
        OsmConfig.getInstance().userAgentValue = packageName
    }

    /** Enables @HiltWorker injection for GeofenceWorker / GeofenceReRegisterWorker. */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
