package pl.tinks.budgetbuddy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Orange = Color(0xFFFF5900)
private val White = Color(0xFFFFFDFC)
private val Black = Color(0xFF171717)

private val LightColorScheme = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFF772B),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFF9A64),
    onSecondary = Color(0xFFFFF7F7),
    secondaryContainer = Color(0x22FF8C00),
    background = White,
    onBackground = Color(0xFF6e6e6e),
    surface = Color(0xFFFFF9F5),
    onSurface = Color(0xFF333333),
    surfaceVariant = Color(0xFFDCD2D2),
    onSurfaceVariant = Color(0xFF737373),
    errorContainer = Color(0xFF8F2828)
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFF8642),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFFAF83),
    onSecondary = Color(0xFF262626),
    secondaryContainer = Color(0x3C000000),
    background = Black,
    onBackground = Color(0xFFD9D9D9),
    surface = Color(0xFF232323),
    onSurface = Color(0xFFD9D9D9),
    surfaceVariant = Color(0xFF525252),
    onSurfaceVariant = Color(0xFF969696)

)

@Composable
fun BudgetBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
