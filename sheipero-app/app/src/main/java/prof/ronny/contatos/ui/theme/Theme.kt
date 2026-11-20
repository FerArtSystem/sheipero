package prof.ronny.contatos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SheiperoGreenLime,
    onPrimary = Color(0xFF101311),
    primaryContainer = SheiperoGreenPrimary,
    onPrimaryContainer = Color(0xFFE8FCDD),
    secondary = SheiperoLimeHover,
    onSecondary = Color(0xFF101311),
    background = SheiperoDarkBg,
    onBackground = Color(0xFFF7FAF6),
    surface = SheiperoDarkSurface,
    onSurface = Color(0xFFF7FAF6),
    surfaceContainer = SheiperoDarkCard,
    onSurfaceVariant = Color(0xFFA5B8AA),
    outline = SheiperoMuted
)

private val LightColorScheme = lightColorScheme(
    primary = SheiperoGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF14532D),
    secondary = SheiperoGreenLime,
    onSecondary = Color(0xFF101311),
    secondaryContainer = Color(0xFFE8FCDD),
    onSecondaryContainer = Color(0xFF14532D),
    background = SheiperoPanelLight,
    onBackground = Color(0xFF1B261D),
    surface = Color.White,
    onSurface = Color(0xFF1B261D),
    surfaceContainer = Color(0xFFEEF5ED),
    onSurfaceVariant = SheiperoMuted,
    outline = Color(0xFF9CAD9E)
)

@Composable
fun ContatosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desativado dynamic color para preservar a identidade Sheipero
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SheiperoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ContatosTheme(darkTheme = darkTheme, dynamicColor = false, content = content)
}
