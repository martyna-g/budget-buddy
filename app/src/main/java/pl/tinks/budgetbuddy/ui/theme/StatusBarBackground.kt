package pl.tinks.budgetbuddy.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

@Composable
fun StatusBarBackground(color: Color) {
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)
    val statusBarHeightDp = with(density) { statusBarHeightPx.toDp() }
    Box(
        Modifier
            .fillMaxWidth()
            .height(statusBarHeightDp)
            .background(color)
    )
}
