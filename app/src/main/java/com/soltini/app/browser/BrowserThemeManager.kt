package com.soltini.app.browser

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.soltini.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * BrowserTheme
 *
 * Defines vibrant, colorful color schemes for the Myra Internal Browser.
 */
data class BrowserTheme(
    val id: String,
    val name: String,
    val emoji: String,
    val primary: Color,
    val secondary: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textPrimary: Color = Color(0xFFF1F5F9),
    val textSecondary: Color = Color(0xFF94A3B8),
    val gradientColors: List<Color>
)

enum class WallpaperType {
    DRAWABLE_RES,
    GRADIENT,
    SOLID
}

/**
 * BrowserWallpaper
 *
 * Represents an artistic visual background or procedural gradient for the browser.
 */
data class BrowserWallpaper(
    val id: String,
    val name: String,
    val category: String,
    val type: WallpaperType,
    val drawableRes: Int? = null,
    val gradientColors: List<Color>? = null,
    val solidColor: Color? = null,
    val previewColor: Color
)

class BrowserThemeManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("myra_browser_theme_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: BrowserThemeManager? = null

        fun getInstance(context: Context): BrowserThemeManager {
            return instance ?: synchronized(this) {
                instance ?: BrowserThemeManager(context).also { instance = it }
            }
        }

        val THEMES = listOf(
            BrowserTheme(
                id = "cyberpunk",
                name = "Cyber Neon",
                emoji = "⚡",
                primary = Color(0xFF00F0FF),
                secondary = Color(0xFFFF007F),
                surface = Color(0xFF0A0E1A),
                surfaceVariant = Color(0xFF141C2E),
                border = Color(0xFF00F0FF).copy(alpha = 0.35f),
                gradientColors = listOf(Color(0xFF00F0FF), Color(0xFF8A2BE2), Color(0xFFFF007F))
            ),
            BrowserTheme(
                id = "cosmic_purple",
                name = "Cosmic Nebula",
                emoji = "🌌",
                primary = Color(0xFFA855F7),
                secondary = Color(0xFFEC4899),
                surface = Color(0xFF0D0B18),
                surfaceVariant = Color(0xFF1A162B),
                border = Color(0xFFA855F7).copy(alpha = 0.35f),
                gradientColors = listOf(Color(0xFFA855F7), Color(0xFF6366F1), Color(0xFFEC4899))
            ),
            BrowserTheme(
                id = "sunset_glow",
                name = "Sunset Glow",
                emoji = "🌅",
                primary = Color(0xFFFF6B6B),
                secondary = Color(0xFFFFA502),
                surface = Color(0xFF120E16),
                surfaceVariant = Color(0xFF221A2A),
                border = Color(0xFFFF6B6B).copy(alpha = 0.35f),
                gradientColors = listOf(Color(0xFFFF6B6B), Color(0xFFFF9F43), Color(0xFFFF5252))
            ),
            BrowserTheme(
                id = "emerald_matrix",
                name = "Emerald Matrix",
                emoji = "🌿",
                primary = Color(0xFF10B981),
                secondary = Color(0xFF06B6D4),
                surface = Color(0xFF061412),
                surfaceVariant = Color(0xFF0E2420),
                border = Color(0xFF10B981).copy(alpha = 0.35f),
                gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF34D399))
            ),
            BrowserTheme(
                id = "ocean_azure",
                name = "Ocean Azure",
                emoji = "🌊",
                primary = Color(0xFF38BDF8),
                secondary = Color(0xFF818CF8),
                surface = Color(0xFF0B132B),
                surfaceVariant = Color(0xFF1C2541),
                border = Color(0xFF38BDF8).copy(alpha = 0.35f),
                gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF6366F1))
            ),
            BrowserTheme(
                id = "royal_velvet",
                name = "Royal Velvet",
                emoji = "👑",
                primary = Color(0xFFE879F9),
                secondary = Color(0xFFFB7185),
                surface = Color(0xFF180D1E),
                surfaceVariant = Color(0xFF2A1535),
                border = Color(0xFFE879F9).copy(alpha = 0.35f),
                gradientColors = listOf(Color(0xFFE879F9), Color(0xFFC084FC), Color(0xFFF43F5E))
            ),
            BrowserTheme(
                id = "midnight_black",
                name = "Midnight OLED",
                emoji = "🌑",
                primary = Color(0xFF60A5FA),
                secondary = Color(0xFF94A3B8),
                surface = Color(0xFF030712),
                surfaceVariant = Color(0xFF111827),
                border = Color(0xFF374151),
                gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1E40AF), Color(0xFF0F172A))
            )
        )

        val WALLPAPERS = listOf(
            BrowserWallpaper(
                id = "wp_cyberpunk",
                name = "Cyber City Neon",
                category = "Art",
                type = WallpaperType.DRAWABLE_RES,
                drawableRes = R.drawable.img_wp_cyberpunk,
                previewColor = Color(0xFF00F0FF)
            ),
            BrowserWallpaper(
                id = "wp_cosmic",
                name = "Cosmic Galaxy",
                category = "Art",
                type = WallpaperType.DRAWABLE_RES,
                drawableRes = R.drawable.img_wp_cosmic,
                previewColor = Color(0xFFA855F7)
            ),
            BrowserWallpaper(
                id = "wp_sunset",
                name = "Synthwave Sunset",
                category = "Art",
                type = WallpaperType.DRAWABLE_RES,
                drawableRes = R.drawable.img_wp_sunset,
                previewColor = Color(0xFFFF6B6B)
            ),
            BrowserWallpaper(
                id = "grad_aurora",
                name = "Northern Aurora",
                category = "Gradients",
                type = WallpaperType.GRADIENT,
                gradientColors = listOf(Color(0xFF021B1A), Color(0xFF044343), Color(0xFF0A1128)),
                previewColor = Color(0xFF2DD4BF)
            ),
            BrowserWallpaper(
                id = "grad_deep_galaxy",
                name = "Deep Galaxy Ripple",
                category = "Gradients",
                type = WallpaperType.GRADIENT,
                gradientColors = listOf(Color(0xFF0F051D), Color(0xFF2E1065), Color(0xFF172554)),
                previewColor = Color(0xFF8B5CF6)
            ),
            BrowserWallpaper(
                id = "grad_sunset_sky",
                name = "Sunset Horizon",
                category = "Gradients",
                type = WallpaperType.GRADIENT,
                gradientColors = listOf(Color(0xFF2B092A), Color(0xFF5E1B3E), Color(0xFF1A0A2A)),
                previewColor = Color(0xFFF43F5E)
            ),
            BrowserWallpaper(
                id = "solid_oled",
                name = "Pure OLED Minimal",
                category = "Solid",
                type = WallpaperType.SOLID,
                solidColor = Color(0xFF06090E),
                previewColor = Color(0xFF1E293B)
            )
        )
    }

    private val _currentTheme = MutableStateFlow(getSavedTheme())
    val currentTheme: StateFlow<BrowserTheme> = _currentTheme.asStateFlow()

    private val _currentWallpaper = MutableStateFlow(getSavedWallpaper())
    val currentWallpaper: StateFlow<BrowserWallpaper> = _currentWallpaper.asStateFlow()

    private val _wallpaperDim = MutableStateFlow(prefs.getFloat("wallpaper_dim", 0.45f))
    val wallpaperDim: StateFlow<Float> = _wallpaperDim.asStateFlow()

    private val _adBlockerCount = MutableStateFlow(prefs.getInt("ads_blocked_count", 28))
    val adBlockerCount: StateFlow<Int> = _adBlockerCount.asStateFlow()

    private fun getSavedTheme(): BrowserTheme {
        val savedId = prefs.getString("selected_theme_id", "cyberpunk")
        return THEMES.find { it.id == savedId } ?: THEMES.first()
    }

    private fun getSavedWallpaper(): BrowserWallpaper {
        val savedId = prefs.getString("selected_wallpaper_id", "wp_cyberpunk")
        return WALLPAPERS.find { it.id == savedId } ?: WALLPAPERS.first()
    }

    fun setTheme(theme: BrowserTheme) {
        _currentTheme.value = theme
        prefs.edit().putString("selected_theme_id", theme.id).apply()
    }

    fun setWallpaper(wallpaper: BrowserWallpaper) {
        _currentWallpaper.value = wallpaper
        prefs.edit().putString("selected_wallpaper_id", wallpaper.id).apply()
    }

    fun setWallpaperDim(dim: Float) {
        _wallpaperDim.value = dim.coerceIn(0.1f, 0.85f)
        prefs.edit().putFloat("wallpaper_dim", _wallpaperDim.value).apply()
    }

    fun incrementBlockedAds() {
        val next = _adBlockerCount.value + 1
        _adBlockerCount.value = next
        prefs.edit().putInt("ads_blocked_count", next).apply()
    }
}
