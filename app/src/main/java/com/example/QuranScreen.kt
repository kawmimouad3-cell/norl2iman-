package com.example

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranListScreen(
    language: AppLanguage,
    onNavigateUp: (() -> Unit)? = null,
    onChapterClick: (Int) -> Unit,
    viewModel: QuranViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppTranslation.translate("index", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onNavigateUp != null) {
                        IconButton(onClick = onNavigateUp) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.chapters) { chapter ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChapterClick(chapter.startingPage) },
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = "${AppTranslation.translate("page_label", language)} ${chapter.startingPage}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                    }
                                    
                                    Text(
                                        text = "${AppTranslation.translate("surah_label", language)} ${chapter.name}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}


enum class SheetTab {
    NONE, TAFSIR, TRANSLATION
}

@Composable
fun BottomSheetActionButton(
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    bgColor: Color,
    textColor: Color,
    showLoading: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(bgColor, CircleShape)
                .clip(CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = tint,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = iconVector,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChapterScreen(
    language: AppLanguage,
    chapterId: Int, // Represents the starting page number in the new design
    onNavigateUp: () -> Unit,
    viewModel: QuranViewModel = viewModel()
) {
    val allPages by viewModel.allPages.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = (chapterId - 1).coerceIn(0, 603),
        pageCount = { 604 } // Full Mushaf size
    )

    // Load visible and neighboring pages
    LaunchedEffect(pagerState.currentPage) {
        viewModel.loadPageIfNeeded(pagerState.currentPage + 1)
        if (pagerState.currentPage > 0) viewModel.loadPageIfNeeded(pagerState.currentPage)
        if (pagerState.currentPage < 603) viewModel.loadPageIfNeeded(pagerState.currentPage + 2)
    }

    val context = LocalContext.current

    // Automatically persistent Last Read page and first verse info as pages are scrolled
    LaunchedEffect(pagerState.currentPage, allPages) {
        val pageNumber = pagerState.currentPage + 1
        val page = allPages[pageNumber]
        if (page != null) {
            val firstVerse = page.verses.firstOrNull()
            if (firstVerse != null) {
                val prefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putInt("last_read_page", page.pageNumber)
                    .putString("last_read_surah", firstVerse.surahName)
                    .putInt("last_read_verse", firstVerse.numberInSurah)
                    .putString("last_read_text", firstVerse.text)
                    .putLong("last_read_time", System.currentTimeMillis())
                    .apply()
            }
        }
    }
    var bookmarkedVerseIds by remember {
        mutableStateOf(
            context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
                .getStringSet("bookmarked_verses", emptySet())
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        )
    }

    fun toggleBookmark(verseId: Int) {
        val prefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
        val currentSet = prefs.getStringSet("bookmarked_verses", emptySet()) ?: emptySet()
        val stringId = verseId.toString()
        val newSet = if (currentSet.contains(stringId)) {
            currentSet - stringId
        } else {
            currentSet + stringId
        }
        prefs.edit().putStringSet("bookmarked_verses", newSet).apply()
        bookmarkedVerseIds = newSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    var selectedVerseForTranslation by remember { mutableStateOf<Verse?>(null) }
    var translationText by remember { mutableStateOf("") }
    var translationEnText by remember { mutableStateOf("") }
    var tafsirText by remember { mutableStateOf("") }
    var isFetchingTranslation by remember { mutableStateOf(false) }
    var fetchTranslationError by remember { mutableStateOf<String?>(null) }

    // Customizer States
    var fontSizeMultiplier by remember { mutableStateOf(1.0f) }
    var quranThemeMode by remember { mutableStateOf("ceramic") } // "ceramic", "dark", "white"
    var isCustomizerOpen by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var activePlayingVerseId by remember { mutableStateOf<Int?>(null) }
    var selectedReciter by remember { mutableStateOf("ar.husary") }
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var playError by remember { mutableStateOf<String?>(null) }
    val playerRef = remember { mutableStateOf<android.media.MediaPlayer?>(null) }

    // Clean up mediaPlayer on dispose
    DisposableEffect(Unit) {
        onDispose {
            playerRef.value?.run {
                try {
                    if (this.isPlaying) {
                        stop()
                    }
                } catch (e: Exception) {}
                release()
            }
            playerRef.value = null
        }
    }

    fun playVerse(verseId: Int) {
        // Optimization: We need verse info. We can find it in allPages or use a global verse lookup if we had one.
        // For now, we find the page the verse belongs to.
        // Each surah has a known range of verse IDs. But simplistically, we find it in currently loaded pages or just use verseId.
        
        val playingVerse = allPages.values.flatMap { it.verses }.find { it.id == verseId }
        
        if (playingVerse == null) {
            // Verse not loaded! We probably need to find WHICH page it belongs to.
            // But verseId is consecutive from 1..6236.
            // We can skip searching if we don't have it loaded yet, 
            // or just rely on the fact that if it's playing, its page is likely loaded or loading.
            return 
        }

        activePlayingVerseId = verseId
        isPlaying = false
        isBuffering = true
        playError = null

        playerRef.value?.run {
            try {
                if (this.isPlaying) {
                    stop()
                }
            } catch (e: Exception) {}
            release()
        }
        playerRef.value = null

        val surahPadded = String.format("%03d", verse.surahNumber)
        val ayahPadded = String.format("%03d", verse.numberInSurah)
        val filename = "$surahPadded$ayahPadded.mp3"

        val everyAyahFolder = when (selectedReciter) {
            "ar.husary" -> "Al_Husary_128kbps"
            "ar.minshawi" -> "Minshawy_Murattal_128kbps"
            "ar.alafasy" -> "Alafasy_128kbps"
            else -> "Al_Husary_128kbps"
        }

        val urls = listOf(
            "https://cdn.islamic.network/quran/audio/128/$selectedReciter/$verseId.mp3",
            "https://everyayah.com/data/$everyAyahFolder/$filename",
            "https://mirrors.quranicaudio.com/everyayah/data/$everyAyahFolder/$filename"
        )

        fun playWithFallback(index: Int) {
            if (index >= urls.size) {
                isBuffering = false
                isPlaying = false
                playError = "عذراً، تعذر الاتصال بخوادم التلاوة الصوتية. يرجى التحقق من اتصالك بالإنترنت."
                return
            }

            val attemptUrl = urls[index]
            isBuffering = true
            playError = null

            try {
                val mp = android.media.MediaPlayer().apply {
                    setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(attemptUrl)
                    setOnPreparedListener {
                        isBuffering = false
                        start()
                        isPlaying = true
                        
                        // Auto scroll to the page matching this verse!
                        val pageIdx = allPages.indexOfFirst { p -> p.verses.any { it.id == verseId } }
                        if (pageIdx != -1 && pageIdx != pagerState.currentPage) {
                            scope.launch {
                                pagerState.animateScrollToPage(pageIdx)
                            }
                        }
                    }
                    setOnCompletionListener {
                        isPlaying = false
                        val nextVerseId = verseId + 1
                        if (nextVerseId <= 6236) {
                            playVerse(nextVerseId)
                        } else {
                            activePlayingVerseId = null
                        }
                    }
                    setOnErrorListener { _, _, _ ->
                        release()
                        playWithFallback(index + 1)
                        true
                    }
                }
                mp.prepareAsync()
                playerRef.value = mp
            } catch (e: Exception) {
                playWithFallback(index + 1)
            }
        }

        playWithFallback(0)
    }

    LaunchedEffect(selectedReciter) {
        activePlayingVerseId?.let { verseId ->
            if (isPlaying || isBuffering) {
                playVerse(verseId)
            }
        }
    }

    // Dynamic Theme Coloring Mappings based on customizer choices
    val bgPageColor = when (quranThemeMode) {
        "dark" -> Color(0xFF121212) // Pitch premium dark black
        "white" -> Color(0xFFFAFAFA)
        else -> Color(0xFFFDF7E7) // Ceramic
    }
    val textPageColor = when (quranThemeMode) {
        "dark" -> Color(0xFFEFF7F4)
        "white" -> Color(0xFF111111)
        else -> Color.Black
    }
    val borderColor = when (quranThemeMode) {
        "dark" -> Color(0xFFE8C36A)
        "white" -> Color(0xFFE0E0E0)
        else -> Color(0xFFD4B872)
    }
    val topHeaderColor = when (quranThemeMode) {
        "dark" -> Color(0xFFE8C36A)
        "white" -> Color(0xFF333333)
        else -> Color(0xFF8B4513)
    }
    val headerBgColor = when (quranThemeMode) {
        "dark" -> Color(0xFF222222) // Sleek matching dark background container
        "white" -> Color(0xFFEEEEEE)
        else -> Color(0xFFF3E5AB)
    }

    LaunchedEffect(selectedVerseForTranslation) {
        selectedVerseForTranslation?.let { verse ->
            isFetchingTranslation = true
            fetchTranslationError = null
            translationText = ""
            translationEnText = ""
            tafsirText = ""
            viewModel.fetchVerseTranslationAndTafsir(verse.id).fold(
                onSuccess = { (translationFr, translationEn, tafsir) ->
                    translationText = translationFr
                    translationEnText = translationEn
                    tafsirText = tafsir
                    isFetchingTranslation = false
                },
                onFailure = { err ->
                    fetchTranslationError = "تعذر تحميل الترجمة والتفسير. تأكد من الاتصال بالإنترنت."
                    isFetchingTranslation = false
                }
            )
        }
    }

    fun Int.toArabicNumber(): String {
        val arabicNumbers = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return this.toString().map { char ->
            if (char.isDigit()) arabicNumbers[char.toString().toInt()] else char
        }.joinToString("")
    }

    Scaffold(
        containerColor = bgPageColor
    ) { innerPadding ->
        if (uiState.chapters.isNotEmpty()) {
            val quranFont = rememberQcfFontFamily("QCF4_Hafs_01")
            
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                    // Top header block resembling Mushaf headers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الجزء", // Can derive Juz later if needed
                            style = MaterialTheme.typography.titleMedium.copy(color = topHeaderColor)
                        )
                        
                        // Let's use the first verse's surah name for header
                        val currentPageNumber = pagerState.currentPage + 1
                        val pageData = allPages[currentPageNumber]
                        val surahName = pageData?.verses?.firstOrNull()?.surahName ?: ""
                        Text(
                            text = "سورة $surahName",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = topHeaderColor
                            )
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Customizer Toggle Button
                            IconButton(
                                onClick = { isCustomizerOpen = !isCustomizerOpen },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Palette, 
                                    contentDescription = "مظهر المصحف", 
                                    tint = topHeaderColor
                                )
                            }
                            
                            IconButton(onClick = onNavigateUp, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack, 
                                    contentDescription = "رجوع", 
                                    tint = topHeaderColor
                                )
                            }
                        }
                    }

                    // Collapsible Appearance Customizer Panel
                    if (isCustomizerOpen) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = headerBgColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Font size scale setting
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "تخصيص حجم الخط:",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = topHeaderColor
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IconButton(
                                            onClick = { if (fontSizeMultiplier > 0.7f) fontSizeMultiplier = (fontSizeMultiplier - 0.1f).coerceAtLeast(0.7f) },
                                            colors = IconButtonDefaults.iconButtonColors(containerColor = bgPageColor),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Text("-A", color = textPageColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Text(
                                            text = "${(fontSizeMultiplier * 100).toInt()}%",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = textPageColor
                                        )
                                        IconButton(
                                            onClick = { if (fontSizeMultiplier < 2.0f) fontSizeMultiplier = (fontSizeMultiplier + 0.1f).coerceAtMost(2.0f) },
                                            colors = IconButtonDefaults.iconButtonColors(containerColor = bgPageColor),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Text("+A", color = textPageColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }

                                HorizontalDivider(color = borderColor.copy(alpha = 0.3f), thickness = 1.dp)

                                // Comfort Mode Style Theme selection
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المظهر المريح:",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = topHeaderColor
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            Triple("ceramic", "سيراميك", Color(0xFFFDF7E7)),
                                            Triple("dark", "داكن", Color(0xFF121212)),
                                            Triple("white", "ناصع", Color(0xFFFFFFFF))
                                        ).forEach { (mode, name, previewColor) ->
                                            val isSelected = quranThemeMode == mode
                                            Button(
                                                onClick = { quranThemeMode = mode },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isSelected) topHeaderColor else previewColor,
                                                    contentColor = if (isSelected) bgPageColor else if (mode == "dark") Color.White else Color.Black
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, borderColor) else null,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { pageIndex ->
                        val pageNumber = pageIndex + 1
                        val page = allPages[pageNumber]
                        
                        if (page == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = topHeaderColor)
                            }
                        } else {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .background(bgPageColor)
                                .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(4.dp))
                                .padding(4.dp)
                                .border(width = 1.dp, color = borderColor.copy(alpha = 0.7f), shape = RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        ) {
                            val density = androidx.compose.ui.platform.LocalDensity.current
                            val totalVisualLines = (page.lines.size + 2).coerceAtLeast(10)
                            val dynamicLineHeight = with(density) { (maxHeight / totalVisualLines.toFloat()).toSp() } * fontSizeMultiplier
                            val dynamicFontSize = dynamicLineHeight * 0.76f
                            val verseLookup = remember(page.verses) { page.verses.associateBy { it.verseKey } }
                            val activeVerseKey = allPages.values
                                .flatMap { it.verses }
                                .find { it.id == activePlayingVerseId }
                                ?.verseKey

                            val scrollState = key(page.pageNumber) { rememberScrollState() }
                            LaunchedEffect(pagerState.currentPage) {
                                if (pagerState.currentPage == pageIndex) {
                                    scrollState.scrollTo(0)
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(scrollState)
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                page.lines.forEach { line ->
                                    val isSingleGlyphLine = line.words.size == 1
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 1.dp),
                                        horizontalArrangement = if (isSingleGlyphLine) Arrangement.Center else Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        line.words.forEach { word ->
                                            val wordFontFamily = rememberQcfFontFamily(word.fontName)
                                            val tappedVerse = word.verseKey?.let { verseLookup[it] }
                                            val isActiveWord = word.verseKey != null && word.verseKey == activeVerseKey
                                            val fontSize = when (word.type) {
                                                "surah_header" -> dynamicFontSize * 1.18f
                                                "bismillah" -> dynamicFontSize * 1.06f
                                                "end" -> dynamicFontSize * 0.98f
                                                "quarter" -> dynamicFontSize * 1.02f
                                                else -> dynamicFontSize
                                            }

                                            Box(
                                                modifier = if (isSingleGlyphLine) {
                                                    Modifier
                                                        .padding(vertical = 2.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(
                                                            if (isActiveWord) {
                                                                if (quranThemeMode == "dark") Color(0xFF1B4D22) else Color(0xFFFFF9C4)
                                                            } else {
                                                                Color.Transparent
                                                            }
                                                        )
                                                        .clickable(enabled = tappedVerse != null) {
                                                            if (tappedVerse != null) {
                                                                selectedVerseForTranslation = tappedVerse
                                                                val prefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
                                                                prefs.edit()
                                                                    .putInt("last_read_page", page.pageNumber)
                                                                    .putString("last_read_surah", tappedVerse.surahName)
                                                                    .putInt("last_read_verse", tappedVerse.numberInSurah)
                                                                    .putString("last_read_text", tappedVerse.text)
                                                                    .putLong("last_read_time", System.currentTimeMillis())
                                                                    .apply()
                                                            }
                                                        }
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                } else {
                                                    Modifier
                                                        .weight(1f, fill = true)
                                                        .padding(horizontal = 1.dp, vertical = 2.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(
                                                            if (isActiveWord) {
                                                                if (quranThemeMode == "dark") Color(0xFF1B4D22) else Color(0xFFFFF9C4)
                                                            } else {
                                                                Color.Transparent
                                                            }
                                                        )
                                                        .clickable(enabled = tappedVerse != null) {
                                                            if (tappedVerse != null) {
                                                                selectedVerseForTranslation = tappedVerse
                                                                val prefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
                                                                prefs.edit()
                                                                    .putInt("last_read_page", page.pageNumber)
                                                                    .putString("last_read_surah", tappedVerse.surahName)
                                                                    .putInt("last_read_verse", tappedVerse.numberInSurah)
                                                                    .putString("last_read_text", tappedVerse.text)
                                                                    .putLong("last_read_time", System.currentTimeMillis())
                                                                    .apply()
                                                            }
                                                        }
                                                        .padding(horizontal = 2.dp, vertical = 2.dp)
                                                },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = word.glyph.ifBlank { word.text },
                                                    fontFamily = wordFontFamily,
                                                    fontSize = fontSize,
                                                    color = when (word.type) {
                                                        "surah_header" -> topHeaderColor
                                                        "end", "quarter" -> if (quranThemeMode == "dark") Color(0xFFE8C36A) else Color(0xFF8B4513)
                                                        else -> textPageColor
                                                    },
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }

                                if (activePlayingVerseId != null) {
                                    Spacer(modifier = Modifier.height(110.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
                    
                    // OUR STICKY BOTTOM PLAYER BAR OVERLAY
                    if (activePlayingVerseId != null) {
                        val playingVerse = allPages.values.flatMap { it.verses }.find { it.id == activePlayingVerseId }
                        if (playingVerse != null) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = headerBgColor,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                                shadowElevation = 8.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "تلاوة سُورَةُ ${playingVerse.surahName}",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = topHeaderColor
                                            )
                                            Text(
                                                text = "الآية رقم ${playingVerse.numberInSurah} (الصفحة ${playingVerse.pageNumber})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = textPageColor.copy(alpha = 0.7f)
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            listOf(
                                                "ar.husary" to "الحصري",
                                                "ar.minshawi" to "المنشاوي",
                                                "ar.alafasy" to "العفاسي"
                                            ).forEach { (identifier, name) ->
                                                val isSel = selectedReciter == identifier
                                                Text(
                                                    text = name,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isSel) topHeaderColor else textPageColor.copy(alpha = 0.4f),
                                                    modifier = Modifier
                                                        .clickable { selectedReciter = identifier }
                                                        .background(
                                                            color = if (isSel) topHeaderColor.copy(alpha = 0.15f) else Color.Transparent,
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        IconButton(
                                            onClick = {
                                                playerRef.value?.run {
                                                    try { stop() } catch(e: Exception) {}
                                                    release()
                                                }
                                                playerRef.value = null
                                                activePlayingVerseId = null
                                                isPlaying = false
                                                isBuffering = false
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = "إغلاق",
                                                tint = textPageColor.copy(alpha = 0.6f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val hasPrev = activePlayingVerseId!! > 1
                                        val hasNext = activePlayingVerseId!! < 6236

                                        IconButton(
                                            onClick = { playVerse(activePlayingVerseId!! - 1) },
                                            enabled = hasPrev,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.SkipNext,
                                                contentDescription = "السابق",
                                                tint = if (hasPrev) topHeaderColor else textPageColor.copy(alpha = 0.3f)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            if (isBuffering) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(36.dp),
                                                    color = topHeaderColor,
                                                    strokeWidth = 3.dp
                                                )
                                            } else {
                                                IconButton(
                                                    onClick = {
                                                        if (isPlaying) {
                                                            playerRef.value?.pause()
                                                            isPlaying = false
                                                        } else {
                                                            if (playerRef.value == null) {
                                                                playVerse(activePlayingVerseId!!)
                                                            } else {
                                                                playerRef.value?.start()
                                                                isPlaying = true
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .background(topHeaderColor, CircleShape)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                                        contentDescription = "تشغيل / إيقاف مؤقت",
                                                        tint = bgPageColor,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        IconButton(
                                            onClick = { playVerse(activePlayingVerseId!! + 1) },
                                            enabled = hasNext,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.SkipPrevious,
                                                contentDescription = "التالي",
                                                tint = if (hasNext) topHeaderColor else textPageColor.copy(alpha = 0.3f)
                                            )
                                        }
                                    }

                                    playError?.let { err ->
                                        Text(
                                            text = err,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Display Gorgeous ModalBottomSheet with Translation, Tafsir, Bookmarks, and Audio Reciter when a verse is tapped
                    if (selectedVerseForTranslation != null) {
                val verse = selectedVerseForTranslation!!
                ModalBottomSheet(
                    onDismissRequest = { selectedVerseForTranslation = null },
                    containerColor = bgPageColor,
                    contentColor = textPageColor,
                    tonalElevation = 6.dp,
                    dragHandle = {
                        BottomSheetDefaults.DragHandle(
                            color = topHeaderColor.copy(alpha = 0.4f)
                        )
                    }
                ) {
                    var selectedSheetTab by remember { mutableStateOf(SheetTab.NONE) }
                    
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Title reference exactly like the screenshot
                            Text(
                                text = "(${verse.numberInSurah.toArabicNumber()}) سورة ${verse.surahName}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = quranFont,
                                    color = topHeaderColor
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Original verse text
                            Text(
                                text = verse.text,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = quranFont,
                                    lineHeight = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                color = textPageColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )

                            HorizontalDivider(color = borderColor.copy(alpha = 0.3f), thickness = 1.dp)

                            // Clean, premium design action row, arranged Right to Left
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. مشاركة (Share)
                                BottomSheetActionButton(
                                    iconVector = Icons.Rounded.Share,
                                    label = "مشاركة",
                                    tint = topHeaderColor,
                                    bgColor = headerBgColor.copy(alpha = 0.5f),
                                    textColor = textPageColor
                                ) {
                                    val shareText = """
                                        📖 *سورة ${verse.surahName} - الآية ${verse.numberInSurah}* 📖
                                        
                                        قال الله تعالى:
                                        «${verse.text}»
                                        
                                        📝 *التفسير الميسر:*
                                        ${tafsirText.ifEmpty { "المحتوى قيد التحميل من الإنترنت..." }}
                                        
                                        🌍 *Traduction:*
                                        ${translationText.ifEmpty { "Chargement de la traduction..." }}
                                        
                                        تمت المشاركة من تطبيق نور الإيمان.
                                    """.trimIndent()
                                    
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "مشاركة الآية الكريمة"))
                                }

                                // 2. حفظ (Bookmark / Toggle)
                                val isBookmarked = bookmarkedVerseIds.contains(verse.id)
                                BottomSheetActionButton(
                                    iconVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                    label = if (isBookmarked) "محفوظ" else "حفظ",
                                    tint = if (isBookmarked) bgPageColor else topHeaderColor,
                                    bgColor = if (isBookmarked) topHeaderColor else headerBgColor.copy(alpha = 0.5f),
                                    textColor = textPageColor
                                ) {
                                    toggleBookmark(verse.id)
                                }

                                // 3. ترجمة (Translation)
                                BottomSheetActionButton(
                                    iconVector = Icons.Rounded.Translate,
                                    label = "ترجمة",
                                    tint = if (selectedSheetTab == SheetTab.TRANSLATION) bgPageColor else topHeaderColor,
                                    bgColor = if (selectedSheetTab == SheetTab.TRANSLATION) topHeaderColor else headerBgColor.copy(alpha = 0.5f),
                                    textColor = textPageColor
                                ) {
                                    selectedSheetTab = if (selectedSheetTab == SheetTab.TRANSLATION) SheetTab.NONE else SheetTab.TRANSLATION
                                }

                                // 4. تفسير (Tafsir)
                                BottomSheetActionButton(
                                    iconVector = Icons.Rounded.MenuBook,
                                    label = "تفسير",
                                    tint = if (selectedSheetTab == SheetTab.TAFSIR) bgPageColor else topHeaderColor,
                                    bgColor = if (selectedSheetTab == SheetTab.TAFSIR) topHeaderColor else headerBgColor.copy(alpha = 0.5f),
                                    textColor = textPageColor
                                ) {
                                    selectedSheetTab = if (selectedSheetTab == SheetTab.TAFSIR) SheetTab.NONE else SheetTab.TAFSIR
                                }

                                // 5. استماع (Listen / Pause)
                                val isThisVersePlaying = activePlayingVerseId == verse.id
                                val isPlayingCurrent = isThisVersePlaying && isPlaying
                                val isBufferingCurrent = isThisVersePlaying && isBuffering

                                BottomSheetActionButton(
                                    iconVector = if (isPlayingCurrent) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    label = if (isPlayingCurrent) "إيقاف" else "استماع",
                                    showLoading = isBufferingCurrent,
                                    tint = if (isThisVersePlaying) bgPageColor else topHeaderColor,
                                    bgColor = if (isThisVersePlaying) topHeaderColor else headerBgColor.copy(alpha = 0.5f),
                                    textColor = textPageColor
                                ) {
                                    if (isThisVersePlaying) {
                                        if (isPlaying) {
                                            playerRef.value?.pause()
                                            isPlaying = false
                                        } else {
                                            playerRef.value?.start()
                                            isPlaying = true
                                        }
                                    } else {
                                        playVerse(verse.id)
                                    }
                                }
                            }

                            // Dynamic sub-sections inside the sheet for Tafsir or Translation
                            AnimatedVisibility(
                                visible = selectedSheetTab != SheetTab.NONE,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    if (isFetchingTranslation) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CircularProgressIndicator(color = topHeaderColor)
                                                Text(
                                                    text = "جارٍ جلب البيانات...",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = textPageColor.copy(alpha = 0.6f)
                                                )
                                            }
                                        }
                                    } else if (fetchTranslationError != null) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = fetchTranslationError!!,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            color = headerBgColor.copy(alpha = 0.08f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.3f))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .heightIn(max = 200.dp)
                                                    .verticalScroll(rememberScrollState())
                                                    .padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (selectedSheetTab == SheetTab.TAFSIR) {
                                                    Text(
                                                        text = "التفسير الميسر:",
                                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = topHeaderColor,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Right
                                                    )
                                                    Text(
                                                        text = tafsirText.ifEmpty { "لا يوجد تفسير متاح لهذه الآية" },
                                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                                        color = textPageColor,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Right
                                                    )
                                                } else if (selectedSheetTab == SheetTab.TRANSLATION) {
                                                    Text(
                                                        text = "Translation (English):",
                                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = topHeaderColor,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Left
                                                    )
                                                    Text(
                                                        text = translationEnText.ifEmpty { "No translation available for this verse" },
                                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                                        color = textPageColor.copy(alpha = 0.8f),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Left
                                                    )
                                                    
                                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = borderColor.copy(alpha = 0.3f))
                                                    
                                                    Text(
                                                        text = "Traduction (French):",
                                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = topHeaderColor,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Left
                                                    )
                                                    Text(
                                                        text = translationText.ifEmpty { "Aucune traduction disponible pour ce verset" },
                                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                                        color = textPageColor.copy(alpha = 0.8f),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Left
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
} else {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isLoading) CircularProgressIndicator()
        else Text("جارٍ التحميل...", color = textPageColor)
    }
}
}
}

// Key data model for Surahs present on page to avoid duplicates
internal data class SurahKey(
    val number: Int,
    val name: String
)

@Composable
fun VerseAudioPlayer(
    verseId: Int,
    surahNumber: Int,
    numberInSurah: Int,
    textPageColor: Color,
    bgPageColor: Color,
    topHeaderColor: Color,
    borderColor: Color
) {
    var selectedReciter by remember { mutableStateOf("ar.husary") } // "ar.husary", "ar.minshawi" or "ar.alafasy"
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    val playerRef = remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var playError by remember { mutableStateOf<String?>(null) }

    // Clean up mediaPlayer on dispose
    DisposableEffect(Unit) {
        onDispose {
            playerRef.value?.run {
                try {
                    if (this.isPlaying) {
                        stop()
                    }
                } catch (e: Exception) {}
                release()
            }
            playerRef.value = null
        }
    }

    // Reset player completely if reciter or verse changes
    LaunchedEffect(selectedReciter, verseId) {
        playerRef.value?.run {
            try {
                if (this.isPlaying) {
                    stop()
                }
            } catch (e: Exception) {}
            release()
        }
        playerRef.value = null
        isPlaying = false
        isBuffering = false
        playError = null
    }

    val surahPadded = String.format("%03d", surahNumber)
    val ayahPadded = String.format("%03d", numberInSurah)
    val filename = "$surahPadded$ayahPadded.mp3"

    val everyAyahFolder = when (selectedReciter) {
        "ar.husary" -> "Al_Husary_128kbps"
        "ar.minshawi" -> "Minshawy_Murattal_128kbps"
        "ar.alafasy" -> "Alafasy_128kbps"
        else -> "Al_Husary_128kbps"
    }

    val quranComFolder = when (selectedReciter) {
        "ar.husary" -> "Husary"
        "ar.minshawi" -> "Minshawi"
        "ar.alafasy" -> "Alafasy"
        else -> "Husary"
    }

    val urls = listOf(
        "https://cdn.islamic.network/quran/audio/128/$selectedReciter/$verseId.mp3",
        "https://everyayah.com/data/$everyAyahFolder/$filename",
        "https://mirrors.quranicaudio.com/everyayah/data/$everyAyahFolder/$filename"
    )

    fun playWithFallback(index: Int) {
        if (index >= urls.size) {
            isBuffering = false
            isPlaying = false
            playError = "عذراً، تعذر الاتصال بجميع خوادم التلاوة الصوتية. يرجى التحقق من اتصالك بالإنترنت."
            return
        }

        val attemptUrl = urls[index]
        isBuffering = true
        playError = null

        try {
            val mp = android.media.MediaPlayer().apply {
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(attemptUrl)
                setOnPreparedListener {
                    isBuffering = false
                    start()
                    isPlaying = true
                }
                setOnCompletionListener {
                    isPlaying = false
                }
                setOnErrorListener { _, _, _ ->
                    release()
                    // Try next url on main thread
                    playWithFallback(index + 1)
                    true
                }
            }
            mp.prepareAsync()
            playerRef.value = mp
        } catch (e: Exception) {
            playWithFallback(index + 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(topHeaderColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .border(1.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تلاوة صوتية للآية:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = topHeaderColor,
                textAlign = TextAlign.Right
            )
            
            // Reciter quick options
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "ar.husary" to "الحصري",
                    "ar.minshawi" to "المنشاوي",
                    "ar.alafasy" to "العفاسي"
                ).forEach { (identifier, name) ->
                    val isSelected = selectedReciter == identifier
                    Button(
                        onClick = { selectedReciter = identifier },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) topHeaderColor else bgPageColor,
                            contentColor = if (isSelected) bgPageColor else textPageColor
                        ),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)) else null,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = topHeaderColor,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("جاري التحميل...", fontSize = 11.sp, color = textPageColor.copy(alpha = 0.6f))
            } else {
                IconButton(
                    onClick = {
                        if (isPlaying) {
                            playerRef.value?.pause()
                            isPlaying = false
                        } else {
                            if (playerRef.value == null) {
                                playWithFallback(0)
                            } else {
                                playerRef.value?.start()
                                isPlaying = true
                            }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(topHeaderColor, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "تحكم التلاوة",
                        tint = bgPageColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        playError?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            )
        }
    }
}
