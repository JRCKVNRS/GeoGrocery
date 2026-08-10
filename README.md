# GeoGrocery

Native Android app (Kotlin + Jetpack Compose) for **location-based grocery lists**. Link a list to
a shop, and when you physically arrive the app fires a background geofence trigger and posts a
notification with a preview of the still-open items. Tapping it deep-links straight into that list.

## Highlights

- **100% free geocoding** — location autocomplete via OpenStreetMap **Nominatim**, no API key, no
  billing. A unique `User-Agent` is attached to every request per the Nominatim Usage Policy
  (`di/NetworkModule.kt`).
- **Reliable background detection** — Google **Geofencing API** (`play-services-location`). Events
  are handled by a `BroadcastReceiver` that hands off to a `WorkManager` worker, so they survive
  the process being killed and are re-registered after reboot (`RECEIVE_BOOT_COMPLETED`).
- **Completion disarms the geofence** — marking a list *Afgehandeld* (or turning its reminder off)
  immediately removes the geofence; the repository keeps Room and the geofence registration in sync.

## Architecture

Clean Architecture + MVVM with unidirectional data flow.

```
ui/            Compose screens + ViewModels (StateFlow, collectAsStateWithLifecycle)
  navigation/  NavHost, routes, notification deep link (geogrocery://list/{listId})
  permissions/ Runtime permission gate (fine + background location, POST_NOTIFICATIONS)
domain/        Framework-free models + repository contracts
data/
  local/       Room database, DAO, entities (grocery_lists, list_items)
  remote/      Retrofit Nominatim API + DTOs (Moshi)
  repository/  GroceryRepositoryImpl (Room ⇄ geofence sync), LocationSearchRepositoryImpl
  mapper/      Entity ⇆ domain mappers
geofence/      GeofenceManager, receivers (geofence + boot), WorkManager workers, NotificationHelper
di/            Hilt modules (Database, Network, Repository, Dispatcher)
```

**Data flow:** Compose → ViewModel intent → Repository (suspend, `Dispatchers.IO`) → Room. Room
`Flow`s are the single source of truth; the UI observes them and re-renders. Any write that changes
whether a list should be "armed" calls through `GeofenceManager`.

## Permissions

Requested at runtime in the order Android requires (`ui/permissions/PermissionGate.kt`):
1. `ACCESS_FINE_LOCATION` (+ `POST_NOTIFICATIONS` on Android 13+)
2. `ACCESS_BACKGROUND_LOCATION` afterwards — required for geofencing while the app is closed
   (Android 10+). Geofences are only armed once these are granted.

## Building

There is no committed Gradle wrapper JAR (binary). Either open the project in **Android Studio**
(it will provision the wrapper automatically), or from a machine with Gradle installed run:

```bash
gradle wrapper --gradle-version 8.11.1
./gradlew assembleDebug
```

Requires JDK 17, Android SDK 35. `minSdk` is 26.

### Trying the geofence without traveling

Use the emulator's **Extended controls → Location** to set a point inside a list's radius, or push a
mock location with `adb`, to trigger `GEOFENCE_TRANSITION_ENTER`.

## Notes / next steps

- Editing a list's linked *location* after creation is not yet wired into the UI (title, items,
  reminder and completion are). The repository already supports it via `saveList`.
- Nominatim is rate-limited (max 1 req/s); the search box debounces at 350 ms. For heavy use, host
  your own Nominatim instance and change `NominatimApi.BASE_URL`.
