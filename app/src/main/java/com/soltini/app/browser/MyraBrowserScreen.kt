package com.soltini.app.browser

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soltini.app.media.MyraPlaybackContext
import com.soltini.app.ui.theme.RedError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QuickShortcut(
    val name: String,
    val url: String,
    val iconLabel: String,
    val gradient: List<Color>
)

/**
 * MyraBrowserScreen
 *
 * Ultra-modern, highly customizable and vibrant internal browser screen for MYRA.
 * Features:
 * - Dynamic color themes (Cyber Neon, Cosmic Nebula, Sunset Glow, Emerald Matrix, etc.)
 * - Rich wallpaper system (Cyberpunk art, Cosmic Galaxy, Synthwave Sunset, Aurora Gradients)
 * - Real-time Ad-blocker & Privacy Shield status
 * - Integrated live Media Player bar (YouTube & media playback with mini controls)
 * - Quick shortcuts with customizable user bookmarks
 * - One-tap Voice AI prompt chips
 * - Password Vault manager
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyraBrowserScreen(
    onOpenVoiceAi: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val browserController = remember { BrowserController.getInstance(context) }
    val themeManager = remember { BrowserThemeManager.getInstance(context) }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val currentTheme by themeManager.currentTheme.collectAsStateWithLifecycle()
    val currentWallpaper by themeManager.currentWallpaper.collectAsStateWithLifecycle()
    val wallpaperDim by themeManager.wallpaperDim.collectAsStateWithLifecycle()
    val adBlockCount by themeManager.adBlockerCount.collectAsStateWithLifecycle()

    val currentUrl by browserController.currentUrl.collectAsStateWithLifecycle()
    val pageTitle by browserController.pageTitle.collectAsStateWithLifecycle()
    val isLoading by browserController.isLoading.collectAsStateWithLifecycle()
    val statusMessage by browserController.statusMessage.collectAsStateWithLifecycle()
    val canGoBack by browserController.canGoBack.collectAsStateWithLifecycle()
    val canGoForward by browserController.canGoForward.collectAsStateWithLifecycle()
    val isDesktopMode by browserController.isDesktopMode.collectAsStateWithLifecycle()

    val playbackInfo by MyraPlaybackContext.state.collectAsStateWithLifecycle()

    var urlInputText by remember { mutableStateOf("") }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showVaultDialog by remember { mutableStateOf(false) }
    var showAddShortcutDialog by remember { mutableStateOf(false) }

    var currentTimeStr by remember { mutableStateOf("") }
    var currentDateStr by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTimeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now)
            currentDateStr = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now)
            delay(1000L)
        }
    }

    LaunchedEffect(currentUrl) {
        if (currentUrl.isNotBlank() && currentUrl != "about:blank") {
            urlInputText = currentUrl
        }
    }

    BackHandler(enabled = canGoBack) {
        browserController.goBackManual()
    }

    val customShortcuts = remember {
        mutableStateListOf(
            QuickShortcut("YouTube", "https://m.youtube.com", "▶", listOf(Color(0xFFFF0000), Color(0xFFCC0000))),
            QuickShortcut("Google", "https://www.google.com", "G", listOf(Color(0xFF4285F4), Color(0xFF34A853))),
            QuickShortcut("Spotify", "https://open.spotify.com", "♫", listOf(Color(0xFF1DB954), Color(0xFF128C7E))),
            QuickShortcut("Instagram", "https://www.instagram.com", "📷", listOf(Color(0xFF833AB4), Color(0xFFFD1D1D))),
            QuickShortcut("ChatGPT", "https://chatgpt.com", "AI", listOf(Color(0xFF10A37F), Color(0xFF0D8C6D))),
            QuickShortcut("Netflix", "https://www.netflix.com", "N", listOf(Color(0xFFE50914), Color(0xFF990000))),
            QuickShortcut("GitHub", "https://github.com", "GH", listOf(Color(0xFF6E5494), Color(0xFF24292E))),
            QuickShortcut("Amazon", "https://www.amazon.in", "🛍", listOf(Color(0xFFFF9900), Color(0xFFFF6600)))
        )
    }

    val voiceAiPrompts = remember {
        listOf(
            "🎵 Arijit Singh ke songs chalao",
            "🚀 Latest AI technology news",
            "🐍 Python tutorial YouTube",
            "🌦️ Delhi weather report",
            "🎬 Trending movie trailers",
            "🪙 Bitcoin & Crypto price"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .background(currentTheme.surface)
    ) {
        // ── 0. Background Wallpaper Layer ──────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentWallpaper.type) {
                WallpaperType.DRAWABLE_RES -> {
                    currentWallpaper.drawableRes?.let { resId ->
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = "Wallpaper",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                WallpaperType.GRADIENT -> {
                    val colors = currentWallpaper.gradientColors ?: listOf(Color.Black, Color(0xFF111827))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(colors))
                    )
                }
                WallpaperType.SOLID -> {
                    val col = currentWallpaper.solidColor ?: Color(0xFF050811)
                    Box(modifier = Modifier.fillMaxSize().background(col))
                }
            }

            // Darkening overlay for maximum readability & contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = wallpaperDim))
            )
        }

        // ── Main Column Content ────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            // ── 1. Top Omnibox & Navigation Bar ───────────────────────────────
            Surface(
                color = currentTheme.surface.copy(alpha = 0.88f),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = currentTheme.border)
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Back
                        IconButton(
                            onClick = { browserController.goBackManual() },
                            enabled = canGoBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (canGoBack) currentTheme.textPrimary else currentTheme.textSecondary.copy(alpha = 0.35f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Forward
                        IconButton(
                            onClick = { browserController.goForwardManual() },
                            enabled = canGoForward,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Forward",
                                tint = if (canGoForward) currentTheme.textPrimary else currentTheme.textSecondary.copy(alpha = 0.35f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Reload / Stop
                        IconButton(
                            onClick = {
                                if (isLoading) browserController.stop() else browserController.reload()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                                contentDescription = if (isLoading) "Stop" else "Reload",
                                tint = currentTheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Omnibox Text Field
                        OutlinedTextField(
                            value = urlInputText,
                            onValueChange = { urlInputText = it },
                            placeholder = {
                                Text("Search Google or enter address...", fontSize = 12.sp, color = currentTheme.textSecondary)
                            },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = if (currentUrl.startsWith("https")) Icons.Default.Lock else Icons.Default.Search,
                                    contentDescription = "Security",
                                    tint = if (currentUrl.startsWith("https")) currentTheme.secondary else currentTheme.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            trailingIcon = {
                                if (urlInputText.isNotBlank()) {
                                    IconButton(
                                        onClick = { urlInputText = "" },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = currentTheme.textSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    keyboardController?.hide()
                                    browserController.navigateUserUrl(urlInputText)
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = currentTheme.surfaceVariant.copy(alpha = 0.9f),
                                unfocusedContainerColor = currentTheme.surfaceVariant.copy(alpha = 0.8f),
                                focusedBorderColor = currentTheme.primary,
                                unfocusedBorderColor = currentTheme.border,
                                focusedTextColor = currentTheme.textPrimary,
                                unfocusedTextColor = currentTheme.textPrimary
                            ),
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("browser_omnibox")
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Theme / Wallpaper Customizer
                        IconButton(
                            onClick = { showThemeDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Themes & Wallpaper",
                                tint = currentTheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Desktop Mode Toggle
                        IconButton(
                            onClick = { browserController.toggleDesktopMode() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isDesktopMode) Icons.Default.Computer else Icons.Default.PhoneAndroid,
                                contentDescription = "Desktop Site",
                                tint = if (isDesktopMode) currentTheme.primary else currentTheme.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Home Button
                        IconButton(
                            onClick = {
                                urlInputText = ""
                                browserController.navigateUserUrl("about:blank")
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = currentTheme.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // ── Status Bar & Ad-Shield Pill ───────────────────────────
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                    ) {
                        // Ad-Shield Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(currentTheme.primary.copy(alpha = 0.15f))
                                .border(0.5.dp, currentTheme.primary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Ad Shield",
                                tint = currentTheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Shield Active ($adBlockCount blocked)",
                                color = currentTheme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Status message
                        Text(
                            text = if (statusMessage.isNotBlank()) statusMessage else "MYRA Browser Ready",
                            color = currentTheme.textSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Password Vault icon
                        IconButton(
                            onClick = { showVaultDialog = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Vault",
                                tint = currentTheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp),
                        color = currentTheme.primary,
                        trackColor = currentTheme.border
                    )
                }
            }

            // ── 2. Live Media Mini-Player Bar (When audio/video is playing) ─────
            AnimatedVisibility(visible = playbackInfo.title.isNotBlank()) {
                Surface(
                    color = currentTheme.surfaceVariant.copy(alpha = 0.95f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 0.5.dp, color = currentTheme.primary.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(currentTheme.gradientColors)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playbackInfo.isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playbackInfo.title.ifBlank { "Media Playback" },
                                color = currentTheme.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = playbackInfo.artist.ifBlank { "MYRA Internal Browser" },
                                color = currentTheme.secondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }

                        // Play/Pause
                        IconButton(
                            onClick = { browserController.togglePlayPauseMedia() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (playbackInfo.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = currentTheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Next
                        IconButton(
                            onClick = { browserController.nextMediaDirect() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = currentTheme.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Stop
                        IconButton(
                            onClick = { browserController.stopMediaDirect() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = RedError,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // ── 3. Main Viewport (WebView OR Home Dashboard) ───────────────────
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                val isBlank = currentUrl.isBlank() || currentUrl == "about:blank"

                // 3.A Android WebView
                AndroidView(
                    factory = { ctx ->
                        FrameLayout(ctx).apply {
                            val wv = browserController.obtainWebView()
                            (wv.parent as? ViewGroup)?.removeView(wv)
                            browserController.attachContext(ctx)
                            addView(
                                wv,
                                FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            )
                            wv.onResume()
                        }
                    },
                    onRelease = { container ->
                        container.removeAllViews()
                        browserController.detachContext()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isBlank) Color.Transparent else Color.White)
                )

                // 3.B Start / Home Dashboard
                if (isBlank) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(28.dp))

                            // Live Clock & Aesthetic Greeting
                            Text(
                                text = currentTimeStr,
                                color = currentTheme.textPrimary,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentDateStr,
                                color = currentTheme.secondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Floating Glowing Search Bar
                            OutlinedTextField(
                                value = urlInputText,
                                onValueChange = { urlInputText = it },
                                placeholder = {
                                    Text("Google search or website URL...", fontSize = 13.sp, color = currentTheme.textSecondary)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = currentTheme.primary
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        keyboardController?.hide()
                                        browserController.navigateUserUrl(urlInputText)
                                    }
                                ),
                                shape = RoundedCornerShape(26.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = currentTheme.surfaceVariant.copy(alpha = 0.95f),
                                    unfocusedContainerColor = currentTheme.surfaceVariant.copy(alpha = 0.85f),
                                    focusedBorderColor = currentTheme.primary,
                                    unfocusedBorderColor = currentTheme.border,
                                    focusedTextColor = currentTheme.textPrimary,
                                    unfocusedTextColor = currentTheme.textPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                            )

                            Spacer(modifier = Modifier.height(26.dp))

                            // Quick Shortcuts Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quick Launch Shortcuts",
                                    color = currentTheme.textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                TextButton(onClick = { showAddShortcutDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = currentTheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add", color = currentTheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Shortcuts Grid (4 per row)
                        items(customShortcuts.chunked(4)) { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (sc in rowItems) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                browserController.navigateUserUrl(sc.url)
                                            }
                                            .padding(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Brush.linearGradient(sc.gradient))
                                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = sc.iconLabel,
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = sc.name,
                                            color = currentTheme.textPrimary,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Voice AI Quick Action Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = currentTheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "One-Tap Voice Prompts",
                                    color = currentTheme.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (prompt in voiceAiPrompts) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(currentTheme.surfaceVariant.copy(alpha = 0.85f))
                                            .border(1.dp, currentTheme.border, RoundedCornerShape(14.dp))
                                            .clickable {
                                                if (prompt.contains("Arijit") || prompt.contains("songs")) {
                                                    scope.launch { browserController.playMediaOnYouTube("Arijit Singh songs") }
                                                } else if (prompt.contains("AI technology")) {
                                                    scope.launch { browserController.searchWeb("latest AI news") }
                                                } else if (prompt.contains("Python")) {
                                                    scope.launch { browserController.playMediaOnYouTube("Python tutorial full course") }
                                                } else if (prompt.contains("weather")) {
                                                    scope.launch { browserController.searchWeb("Delhi weather today") }
                                                } else if (prompt.contains("trailers")) {
                                                    scope.launch { browserController.playMediaOnYouTube("new movie trailers 2026") }
                                                } else {
                                                    scope.launch { browserController.searchWeb("Bitcoin crypto price today") }
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(text = prompt, color = currentTheme.textPrimary, fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            // Theme & Wallpaper Customization Banner
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = currentTheme.surfaceVariant.copy(alpha = 0.9f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, currentTheme.primary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                                    .clickable { showThemeDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(currentTheme.gradientColors)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Customize Themes & Wallpapers", color = currentTheme.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Current: ${currentTheme.emoji} ${currentTheme.name} • ${currentWallpaper.name}", color = currentTheme.secondary, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { showThemeDialog = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primary)
                                    ) {
                                        Text("Change", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }

            // ── 4. Bottom Modern Navigation Bar ───────────────────────────────
            Surface(
                color = currentTheme.surface.copy(alpha = 0.95f),
                tonalElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = currentTheme.border)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home
                    IconButton(
                        onClick = {
                            urlInputText = ""
                            browserController.navigateUserUrl("about:blank")
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = currentTheme.textPrimary, modifier = Modifier.size(22.dp))
                    }

                    // Themes & Wallpapers
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Wallpaper, contentDescription = "Wallpaper", tint = currentTheme.secondary, modifier = Modifier.size(22.dp))
                    }

                    // Voice AI Mic Button (Center glowing orb)
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(currentTheme.gradientColors))
                            .clickable { onOpenVoiceAi() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Assistant", tint = Color.White, modifier = Modifier.size(26.dp))
                    }

                    // Reload
                    IconButton(
                        onClick = { browserController.reload() },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = currentTheme.textPrimary, modifier = Modifier.size(22.dp))
                    }

                    // Vault
                    IconButton(
                        onClick = { showVaultDialog = true },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Key, contentDescription = "Vault", tint = currentTheme.primary, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }

    // ── Dialog 1: Theme & Wallpaper Customizer ─────────────────────────────────
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = currentTheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Themes & Wallpapers", color = currentTheme.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                    item {
                        Text("1. Color Theme", color = currentTheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Theme selector cards
                    items(BrowserThemeManager.THEMES) { theme ->
                        val isSelected = theme.id == currentTheme.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) currentTheme.primary.copy(alpha = 0.2f) else currentTheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 0.5.dp,
                                    color = if (isSelected) currentTheme.primary else currentTheme.border,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { themeManager.setTheme(theme) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(theme.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(theme.name, color = currentTheme.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(theme.gradientColors))
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("2. Wallpaper & Background", color = currentTheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Wallpaper selector cards
                    items(BrowserThemeManager.WALLPAPERS) { wp ->
                        val isSelected = wp.id == currentWallpaper.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) currentTheme.secondary.copy(alpha = 0.2f) else currentTheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 0.5.dp,
                                    color = if (isSelected) currentTheme.secondary else currentTheme.border,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { themeManager.setWallpaper(wp) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(wp.previewColor)
                                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Wallpaper, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(wp.name, color = currentTheme.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(wp.category, color = currentTheme.textSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("3. Wallpaper Dimming / Contrast", color = currentTheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = wallpaperDim,
                            onValueChange = { themeManager.setWallpaperDim(it) },
                            valueRange = 0.1f..0.85f,
                            colors = SliderDefaults.colors(
                                thumbColor = currentTheme.primary,
                                activeTrackColor = currentTheme.primary,
                                inactiveTrackColor = currentTheme.border
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Apply & Done", color = currentTheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = currentTheme.surface
        )
    }

    // ── Dialog 2: Add Custom Shortcut ──────────────────────────────────────────
    if (showAddShortcutDialog) {
        var newName by remember { mutableStateOf("") }
        var newUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddShortcutDialog = false },
            title = { Text("Add Website Shortcut", color = currentTheme.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Site Name (e.g. Cricbuzz)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = currentTheme.textPrimary,
                            unfocusedTextColor = currentTheme.textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text("URL (e.g. cricbuzz.com)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = currentTheme.textPrimary,
                            unfocusedTextColor = currentTheme.textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newUrl.isNotBlank()) {
                            val fullUrl = if (!newUrl.startsWith("http")) "https://$newUrl" else newUrl
                            val initial = newName.take(2).uppercase()
                            customShortcuts.add(
                                QuickShortcut(
                                    name = newName,
                                    url = fullUrl,
                                    iconLabel = initial,
                                    gradient = currentTheme.gradientColors
                                )
                            )
                            showAddShortcutDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primary)
                ) {
                    Text("Add Shortcut", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShortcutDialog = false }) {
                    Text("Cancel", color = currentTheme.textSecondary)
                }
            },
            containerColor = currentTheme.surface
        )
    }

    // ── Dialog 3: Credential Vault Dialog ──────────────────────────────────────
    if (showVaultDialog) {
        val creds = remember { browserController.getAllCredentials() }
        AlertDialog(
            onDismissRequest = { showVaultDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = currentTheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Myra Password & ID Vault", fontSize = 16.sp, color = currentTheme.textPrimary)
                }
            },
            text = {
                if (creds.isEmpty()) {
                    Text("No saved login credentials yet. Tell Myra: 'Is site ke credentials save kar lo' to store logins securely.", color = currentTheme.textSecondary, fontSize = 13.sp)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                        items(creds.entries.toList()) { (domain, obj) ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = currentTheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(domain, color = currentTheme.secondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("User: " + obj.optString("username"), color = currentTheme.textPrimary, fontSize = 12.sp)
                                        Text("Pass: ••••••••", color = currentTheme.textSecondary, fontSize = 11.sp)
                                    }
                                    IconButton(
                                        onClick = {
                                            browserController.deleteCredential(domain)
                                            showVaultDialog = false
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVaultDialog = false }) {
                    Text("Close", color = currentTheme.primary)
                }
            },
            containerColor = currentTheme.surface
        )
    }
}
