package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import android.location.Geocoder
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.QuranTheme
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay


import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuranTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val quranViewModel: QuranViewModel = viewModel()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"

                var showSettingsDialog by remember { mutableStateOf(false) }
                val context = LocalContext.current
                val uiState by mainViewModel.uiState.collectAsState()

                // Bottom bar is visible on the 5 major portal tabs
                val isTabRoute = currentRoute in listOf("home", "quran_list", "search", "adhkar", "more")

                LaunchedEffect(Unit) {
                    mainViewModel.loadSettings(context)
                    PrayerTimesSyncScheduler.ensureScheduled(context)
                }

                val layoutDirection = if (uiState.language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Scaffold(
                        bottomBar = {
                            if (isTabRoute) {
                                ScaffoldBottomBar(
                                    language = uiState.language,
                                    currentRoute = currentRoute,
                                    onNavigate = { targetRoute ->
                                        if (currentRoute != targetRoute) {
                                            navController.navigate(targetRoute) {
                                                popUpTo("home") {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "splash"
                            ) {
                                composable("splash") {
                                    SplashScreen(onTimeout = {
                                        navController.navigate("home") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    })
                                }
                                composable("home") {
                                    HomeRoute(
                                        mainViewModel = mainViewModel,
                                        onSettingsClick = { showSettingsDialog = true },
                                        onNavigateToQuran = { navController.navigate("quran_list") },
                                        onNavigateToQuranPage = { page -> navController.navigate("chapter/$page") },
                                        onNavigateToAdhkar = { navController.navigate("adhkar") },
                                        onNavigateToQibla = { navController.navigate("qibla") },
                                        onNavigateToTasbih = { navController.navigate("tasbih") }
                                    )
                                }
                                composable("quran_list") {
                                    QuranListScreen(
                                        language = uiState.language,
                                        onNavigateUp = null,
                                        onChapterClick = { chapterId ->
                                            navController.navigate("chapter/$chapterId")
                                        },
                                        viewModel = quranViewModel
                                    )
                                }
                                composable("search") {
                                    SearchScreen(
                                        language = uiState.language,
                                        onChapterClick = { chapterId ->
                                            navController.navigate("chapter/$chapterId")
                                        },
                                        viewModel = quranViewModel
                                    )
                                }
                                composable("adhkar") {
                                    AthkarScreen(
                                        language = uiState.language,
                                        onNavigateUp = null
                                    )
                                }
                                composable("more") {
                                    MoreScreen(
                                        language = uiState.language,
                                        onLanguageChange = { lang -> mainViewModel.setLanguage(lang, context) },
                                        onQiblaClick = { navController.navigate("qibla") },
                                        onTasbihClick = { navController.navigate("tasbih") },
                                        onSettingsClick = { showSettingsDialog = true }
                                    )
                                }
                                composable(
                                    "chapter/{chapterId}",
                                    arguments = listOf(navArgument("chapterId") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val chapterId = backStackEntry.arguments?.getInt("chapterId") ?: 1
                                    ChapterScreen(
                                        language = uiState.language,
                                        chapterId = chapterId,
                                        onNavigateUp = { navController.popBackStack() },
                                        viewModel = quranViewModel
                                    )
                                }
                                composable("qibla") {
                                    val qiblaUiState by mainViewModel.uiState.collectAsState()
                                    QiblaScreen(
                                        language = uiState.language,
                                        latitude = qiblaUiState.latitude,
                                        longitude = qiblaUiState.longitude,
                                        onNavigateUp = { navController.popBackStack() }
                                    )
                                }
                                composable("tasbih") {
                                    TasbihScreen(
                                        language = uiState.language,
                                        onNavigateUp = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }

                    // Shared settings dialog visible to any tab/screen
                    if (showSettingsDialog) {
                        PrayerSettingsDialog(
                            context = context,
                            uiState = uiState,
                            onDismiss = { showSettingsDialog = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScaffoldBottomBar(
    language: AppLanguage,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            val navItems = listOf(
                NavigationItem(AppTranslation.translate("home", language), "home", Icons.Rounded.Home),
                NavigationItem(AppTranslation.translate("quran", language), "quran_list", Icons.AutoMirrored.Rounded.MenuBook),
                NavigationItem(AppTranslation.translate("search", language), "search", Icons.Rounded.Search),
                NavigationItem(AppTranslation.translate("adhkar", language), "adhkar", Icons.Rounded.AccessTime),
                NavigationItem(AppTranslation.translate("more", language), "more", Icons.Rounded.Apps)
            )

            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun PrayerSettingsDialog(
    context: Context,
    uiState: MainUiState,
    onDismiss: () -> Unit
) {
    val language = uiState.language
    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(AppTranslation.translate("ok", language), fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = AppTranslation.translate("settings_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    val prefs = context.getSharedPreferences("prayer_settings", Context.MODE_PRIVATE)
                    var currentMinutes by remember { mutableStateOf(prefs.getInt("pre_prayer_alarm_minutes", 5)) }
                    
                    // Individual Prayer Toggles
                    Text(
                        text = "تفعيل التنبيهات لكل صلاة:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    
                    val prayers = listOf(
                        "fajr" to AppTranslation.translate("fajr", language),
                        "dhuhr" to AppTranslation.translate("dhuhr", language),
                        "asr" to AppTranslation.translate("asr", language),
                        "maghrib" to AppTranslation.translate("maghrib", language),
                        "isha" to AppTranslation.translate("isha", language)
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        prayers.forEach { (key, label) ->
                            var isEnabled by remember { mutableStateOf(prefs.getBoolean("enable_$key", true)) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, style = MaterialTheme.typography.bodyMedium)
                                Switch(
                                    checked = isEnabled,
                                    onCheckedChange = { 
                                        isEnabled = it
                                        prefs.edit().putBoolean("enable_$key", it).apply()
                                        PrayerAlarmScheduler.scheduleUpcomingAlarms(context)
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Muezzin Selection
                    Text(
                        text = "اختر المؤذن:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    
                    val muezzins = AdhanAudioCatalog.options
                    var selectedMuezzinKey by remember {
                        mutableStateOf(
                            prefs.getString("selected_muezzin_key", AdhanAudioCatalog.defaultOption().key)
                                ?: AdhanAudioCatalog.defaultOption().key
                        )
                    }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        muezzins.forEach { muezzin ->
                            val isSelected = selectedMuezzinKey == muezzin.key
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                    .clickable {
                                        selectedMuezzinKey = muezzin.key
                                        prefs.edit().putString("selected_muezzin_key", muezzin.key).apply()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(muezzin.label, style = MaterialTheme.typography.bodyMedium)
                                if (isSelected) {
                                    Icon(Icons.Rounded.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Pre-Prayer Alarm Duration
                    Text(
                        text = AppTranslation.translate("settings_desc", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val alertOptions = listOf(
                            0 to AppTranslation.translate("alarm_off", language),
                            5 to AppTranslation.translate("min_5", language),
                            10 to AppTranslation.translate("min_10", language),
                            15 to AppTranslation.translate("min_15", language),
                            30 to AppTranslation.translate("min_30", language)
                        )

                        alertOptions.forEach { (minutes, label) ->
                            val isSelected = currentMinutes == minutes
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        )
                                        .clickable {
                                            currentMinutes = minutes
                                            prefs.edit().putInt("pre_prayer_alarm_minutes", minutes).apply()
                                            PrayerAlarmScheduler.scheduleUpcomingAlarms(context)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            currentMinutes = minutes
                                            prefs.edit().putInt("pre_prayer_alarm_minutes", minutes).apply()
                                            PrayerAlarmScheduler.scheduleUpcomingAlarms(context)
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }

@Composable
fun HomeRoute(
    mainViewModel: MainViewModel,
    onSettingsClick: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToQuranPage: (Int) -> Unit,
    onNavigateToAdhkar: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToTasbih: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val context = LocalContext.current
        val uiState by mainViewModel.uiState.collectAsState()

        var lastReadPage by remember { mutableStateOf(-1) }
        var lastReadSurah by remember { mutableStateOf("") }
        var lastReadVerse by remember { mutableStateOf(-1) }
        var lastReadText by remember { mutableStateOf("") }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val prefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
                    lastReadPage = prefs.getInt("last_read_page", -1)
                    lastReadSurah = prefs.getString("last_read_surah", "") ?: ""
                    lastReadVerse = prefs.getInt("last_read_verse", -1)
                    lastReadText = prefs.getString("last_read_text", "") ?: ""
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val geocoder = Geocoder(context, Locale("ar"))
                            val addresses = try {
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            } catch (e: Exception) { null }
                            val cityName = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea ?: "إحداثيات (${location.latitude.toInt()}, ${location.longitude.toInt()})"
                            
                            mainViewModel.fetchPrayerTimes(location.latitude, location.longitude, cityName, context)
                        } else {
                            mainViewModel.fetchPrayerTimes(context = context)
                        }
                    }
                } catch (e: SecurityException) {
                    mainViewModel.fetchPrayerTimes(context = context)
                }
            } else {
                mainViewModel.fetchPrayerTimes(context = context)
            }
        }

        // Automatic prayer alarm scheduling when prayerTimes fetched successfully
        LaunchedEffect(uiState.prayerTimes, uiState.cityName) {
            uiState.prayerTimes?.let { times ->
                PrayerAlarmScheduler.scheduleUpcomingAlarms(context)
                val prefs = context.getSharedPreferences("widget_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("fajr", times.fajr)
                    .putString("dhuhr", times.dhuhr)
                    .putString("asr", times.asr)
                    .putString("maghrib", times.maghrib)
                    .putString("isha", times.isha)
                    .putString("city", uiState.cityName)
                    .apply()
                // Update widget
                val intent = android.content.Intent(context, PrayerTimesWidgetProvider::class.java)
                intent.action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = android.appwidget.AppWidgetManager.getInstance(context).getAppWidgetIds(android.content.ComponentName(context, PrayerTimesWidgetProvider::class.java))
                intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                context.sendBroadcast(intent)
            }
        }

        LaunchedEffect(Unit) {
            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            
            val arrayToRequest = if (android.os.Build.VERSION.SDK_INT >= 33) {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

            if (hasCoarse || hasFine) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val geocoder = Geocoder(context, Locale("ar"))
                            val addresses = try {
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            } catch (e: Exception) { null }
                            val cityName = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea ?: "إحداثيات (${location.latitude.toInt()}, ${location.longitude.toInt()})"

                            mainViewModel.fetchPrayerTimes(location.latitude, location.longitude, cityName, context)
                        } else {
                            mainViewModel.fetchPrayerTimes(context = context)
                        }
                    }
                } catch (e: SecurityException) {
                    mainViewModel.fetchPrayerTimes(context = context)
                }
                
                // Request notification permission on 13+ if not granted
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    val hasNotification = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    if (!hasNotification) {
                        permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                    }
                }
            } else {
                mainViewModel.fetchPrayerTimes(context = context)
                permissionLauncher.launch(arrayToRequest)
            }
        }

        HomeScreen(
            uiState = uiState,
            lastReadPage = lastReadPage,
            lastReadSurah = lastReadSurah,
            lastReadVerse = lastReadVerse,
            lastReadText = lastReadText,
            onLastReadClick = { page ->
                if (page >= 1) {
                    onNavigateToQuranPage(page)
                } else {
                    onNavigateToQuran()
                }
            },
            onTick = { mainViewModel.decrementRemainingTime() },
            onSettingsClick = onSettingsClick,
            onQuranClick = onNavigateToQuran,
            onAdhkarClick = onNavigateToAdhkar,
            onQiblaClick = onNavigateToQibla,
            onTasbihClick = onNavigateToTasbih,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeScreen(
    uiState: MainUiState,
    lastReadPage: Int,
    lastReadSurah: String,
    lastReadVerse: Int,
    lastReadText: String,
    onLastReadClick: (Int) -> Unit,
    onTick: () -> Unit,
    onQuranClick: () -> Unit,
    onAdhkarClick: () -> Unit,
    onQiblaClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val language = uiState.language

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HeaderSection(language, uiState.cityName, onSettingsClick)
        DateSection(uiState.dateGregorian)
        PrayerTimeCard(uiState, onTick)
        if (uiState.prayerTimes != null) {
            AllPrayerTimesCard(language, uiState.prayerTimes)
        }
        AyahOfTheDayCard()
        MainServicesGrid(
            onQuranClick = onQuranClick,
            onAdhkarClick = onAdhkarClick,
            onQiblaClick = onQiblaClick,
            onTasbihClick = onTasbihClick
        )
        LastReadCard(
            language = language,
            lastReadPage = lastReadPage,
            lastReadSurah = lastReadSurah,
            lastReadVerse = lastReadVerse,
            lastReadText = lastReadText,
            onClick = onLastReadClick
        )
    }
}

@Composable
fun HeaderSection(language: AppLanguage, cityName: String, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = AppTranslation.translate("app_name", language),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "إعدادات التنبيه",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DateSection(gregorianDate: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "25 ذو الحجة 1447", // Hijri usually requires external library, setting mock for now
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = gregorianDate.ifEmpty { "Chargement..." },
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
fun PrayerTimeCard(uiState: MainUiState, onTick: () -> Unit) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            onTick()
        }
    }

    val language = uiState.language
    val hours = uiState.remainingSeconds / 3600
    val minutes = (uiState.remainingSeconds % 3600) / 60
    val seconds = uiState.remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prayer_time_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    val translatedNextPrayer = AppTranslation.translate(uiState.nextPrayerName, language)
                    Text(
                        text = "${AppTranslation.translate("next_prayer", language)}: $translatedNextPrayer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.errorContainer,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
    
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = AppTranslation.translate("ministry_standards", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = AppTranslation.translate("remaining_time", language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AllPrayerTimesCard(language: AppLanguage, prayerTimes: PrayerTimes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PrayerTimeItem(AppTranslation.translate("fajr", language), prayerTimes.fajr)
            PrayerTimeItem(AppTranslation.translate("dhuhr", language), prayerTimes.dhuhr)
            PrayerTimeItem(AppTranslation.translate("asr", language), prayerTimes.asr)
            PrayerTimeItem(AppTranslation.translate("maghrib", language), prayerTimes.maghrib)
            PrayerTimeItem(AppTranslation.translate("isha", language), prayerTimes.isha)
        }
    }
}

@Composable
fun PrayerTimeItem(name: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

data class AyahOfDay(
    val arabic: String,
    val translation: String,
    val reference: String
)

val dailyAyahs = listOf(
    AyahOfDay("إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Indeed, with hardship [will be] ease.", "الشرح 6 • Al-Sharh 6"),
    AyahOfDay("فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ", "So remember Me; I will remember you. And be grateful to Me and do not deny Me.", "البقرة 152 • Al-Baqarah 152"),
    AyahOfDay("وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ", "And your Lord says, \"Call upon Me; I will respond to you.\"", "غافر 60 • Ghafir 60"),
    AyahOfDay("وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا", "And whoever fears Allah - He will make for him a way out.", "الطلاق 2 • At-Talaq 2"),
    AyahOfDay("إِنَّ اللَّهَ مَعَ الصَّابِرِينَ", "Indeed, Allah is with the patient.", "البقرة 153 • Al-Baqarah 153"),
    AyahOfDay("لَا تَحْزَنْ إِنَّ اللَّهَ مَعَنَا", "Do not grieve; indeed Allah is with us.", "التوبة 40 • At-Tawbah 40"),
    AyahOfDay("الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ", "Those who have believed and whose hearts are assured by the remembrance of Allah. Unquestionably, by the remembrance of Allah hearts are assured.", "الرعد 28 • Ar-Ra'd 28"),
    AyahOfDay("رَبِّ زِدْنِي عِلْمًا", "My Lord, increase me in knowledge.", "طه 114 • Taha 114"),
    AyahOfDay("عَسَىٰ أَن تَكْرَهُوا شَيْئًا وَهُوَ خَيْرٌ لَّكُمْ", "But perhaps you hate a thing and it is good for you.", "البقرة 216 • Al-Baqarah 216")
)

@Composable
fun AyahOfTheDayCard() {
    val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
    val ayah = dailyAyahs[dayOfYear % dailyAyahs.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "آية اليوم • Ayah of the day",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Text(
                text = ayah.arabic,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 40.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = ayah.translation,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = ayah.reference,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class AppService(val titleAr: String, val titleFr: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun MainServicesGrid(
    onQuranClick: () -> Unit,
    onAdhkarClick: () -> Unit,
    onQiblaClick: () -> Unit,
    onTasbihClick: () -> Unit
) {
    val services = listOf(
        AppService("قراءة القرآن", "Quran", Icons.AutoMirrored.Rounded.MenuBook) { onQuranClick() },
        AppService("الأذكار", "Athkar", Icons.Rounded.Spa) { onAdhkarClick() },
        AppService("القبلة", "Qibla", Icons.Rounded.Explore) { onQiblaClick() },
        AppService("تسبيح", "Tasbih", Icons.Rounded.Mosque) { onTasbihClick() }
    )

    Text(
        text = "الخدمات الرئيسية • Services",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.heightIn(max = 400.dp) // Bound the height since it's nested in a verticalScroll
    ) {
        items(services) { service ->
            ServiceCard(service)
        }
    }
}

@Composable
fun ServiceCard(service: AppService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { service.onClick() }
            .testTag("service_card_${service.titleFr}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.titleFr,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = service.titleAr,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = service.titleFr,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun LastReadCard(
    language: AppLanguage,
    lastReadPage: Int,
    lastReadSurah: String,
    lastReadVerse: Int,
    lastReadText: String,
    onClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(lastReadPage) }
            .testTag("last_read_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bookmark,
                            contentDescription = AppTranslation.translate("last_read", language),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = AppTranslation.translate("last_read", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (lastReadPage >= 1) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = AppTranslation.translate("continue", language),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            if (lastReadPage >= 1) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${AppTranslation.translate("surah_label", language)} $lastReadSurah",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${AppTranslation.translate("ayah_label", language)} $lastReadVerse • ${AppTranslation.translate("page_label", language)} $lastReadPage",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        )
                    }

                    if (lastReadText.isNotEmpty()) {
                        Text(
                            text = lastReadText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Serif,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                lineHeight = 32.sp
                            ),
                            textAlign = TextAlign.Right,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppTranslation.translate("no_read_yet", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = AppTranslation.translate("start", language),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
