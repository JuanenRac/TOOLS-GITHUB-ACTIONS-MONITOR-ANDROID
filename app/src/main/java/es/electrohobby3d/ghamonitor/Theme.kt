// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the look: the dark colours of the monitor, one scale of text and the few pieces every screen shares.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object MonitorColors {
    val Background = Color(0xFF0D1117)
    val Surface = Color(0xFF161B22)
    val SurfaceRaised = Color(0xFF1B222C)
    val Outline = Color(0xFF30363D)
    val Accent = Color(0xFF58A6FF)
    val AccentDeep = Color(0xFF2F81F7)
    val Ok = Color(0xFF3FB950)
    val Warning = Color(0xFFD29922)
    val Error = Color(0xFFF85149)
    val Critical = Color(0xFFFF7B72)
    val Text = Color(0xFFE6EDF3)
    val Muted = Color(0xFF9DA7B3)
}

private val Scheme = darkColorScheme(
    primary = MonitorColors.Accent,
    onPrimary = Color(0xFF04111F),
    primaryContainer = Color(0xFF14365C),
    onPrimaryContainer = MonitorColors.Text,
    secondaryContainer = Color(0xFF1C3250),
    onSecondaryContainer = MonitorColors.Text,
    background = MonitorColors.Background,
    onBackground = MonitorColors.Text,
    surface = MonitorColors.Surface,
    onSurface = MonitorColors.Text,
    surfaceVariant = MonitorColors.SurfaceRaised,
    onSurfaceVariant = MonitorColors.Muted,
    surfaceContainer = MonitorColors.Surface,
    outline = MonitorColors.Outline,
    outlineVariant = Color(0xFF21262D),
    error = MonitorColors.Error,
)

private val Scale = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
    labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun MonitorTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = Scale, content = content)
}

/** The mark of the app: a rounded square with the "run" sign. */
@Composable
fun MonitorMark(size: Dp = 36.dp) {
    Box(
        Modifier.size(size).clip(RoundedCornerShape(size * 0.28f)).background(Brush.linearGradient(listOf(MonitorColors.Accent, MonitorColors.AccentDeep))),
        contentAlignment = Alignment.Center,
    ) { Icon(Icons.Filled.PlayArrow, null, tint = Color(0xFF04111F), modifier = Modifier.size(size * 0.7f)) }
}

/** The head of a screen: the mark, the title and a line under it. */
@Composable
fun ScreenTitle(title: String, subtitle: String = "") {
    Row(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        MonitorMark()
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MonitorColors.Text)
            if (subtitle.isNotEmpty()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MonitorColors.Muted)
        }
    }
}

/** A raised card with a thin border; `tint` colours the border and a faint wash with the severity of what it holds. */
@Composable
fun Panel(modifier: Modifier = Modifier, tint: Color? = null, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    val base = modifier.clip(shape)
    Surface(
        modifier = if (onClick != null) base.clickable(onClick = onClick) else base,
        shape = shape,
        color = MonitorColors.SurfaceRaised,
        contentColor = MonitorColors.Text,
        border = BorderStroke(1.dp, tint?.copy(alpha = 0.7f) ?: MonitorColors.Outline),
    ) {
        Column(Modifier.background(Brush.verticalGradient(listOf((tint ?: MonitorColors.Accent).copy(alpha = if (tint != null) 0.10f else 0.03f), Color.Transparent))).padding(12.dp), content = content)
    }
}

/** A big figure with what it counts. */
@Composable
fun StatTile(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Panel(modifier, tint = color.takeIf { value != "0" }) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = if (value == "0") MonitorColors.Muted else color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MonitorColors.Muted, maxLines = 2)
    }
}

/** A small round light next to a word (the colour is never the only sign). */
@Composable
fun StatusDot(color: Color, size: Dp = 10.dp) {
    Box(Modifier.size(size).clip(CircleShape).background(color).border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape))
}
