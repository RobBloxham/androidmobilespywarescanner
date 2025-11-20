package com.spywarescanner.app.ui.theme

import androidx.compose.ui.graphics.Color

// Cybersecurity Neon Dark Theme Colors
object CyberColors {
    // Primary Neon Colors
    val NeonGreen = Color(0xFF00FF88)
    val NeonCyan = Color(0xFF00FFFF)
    val NeonBlue = Color(0xFF0088FF)
    val NeonPurple = Color(0xFF8800FF)
    val NeonPink = Color(0xFFFF0088)
    val NeonOrange = Color(0xFFFF8800)
    val NeonYellow = Color(0xFFFFFF00)
    val NeonRed = Color(0xFFFF0044)

    // Dark Background Colors
    val DarkBackground = Color(0xFF0D1117)
    val DarkSurface = Color(0xFF161B22)
    val DarkSurfaceVariant = Color(0xFF21262D)
    val DarkCard = Color(0xFF1C2128)
    val DarkBorder = Color(0xFF30363D)

    // Text Colors
    val TextPrimary = Color(0xFFF0F6FC)
    val TextSecondary = Color(0xFF8B949E)
    val TextTertiary = Color(0xFF6E7681)

    // Threat Level Colors
    val ThreatCritical = Color(0xFFFF0044)
    val ThreatHigh = Color(0xFFFF4444)
    val ThreatMedium = Color(0xFFFF8800)
    val ThreatLow = Color(0xFFFFCC00)
    val Safe = Color(0xFF00FF88)

    // Gradients (start, end)
    val GradientGreen = listOf(Color(0xFF00FF88), Color(0xFF00CC66))
    val GradientBlue = listOf(Color(0xFF0088FF), Color(0xFF0066CC))
    val GradientPurple = listOf(Color(0xFF8800FF), Color(0xFF6600CC))
    val GradientRed = listOf(Color(0xFFFF0044), Color(0xFFCC0033))
}

// Material 3 Color Scheme
val DarkPrimary = CyberColors.NeonGreen
val DarkOnPrimary = CyberColors.DarkBackground
val DarkPrimaryContainer = Color(0xFF003D22)
val DarkOnPrimaryContainer = CyberColors.NeonGreen

val DarkSecondary = CyberColors.NeonCyan
val DarkOnSecondary = CyberColors.DarkBackground
val DarkSecondaryContainer = Color(0xFF003D3D)
val DarkOnSecondaryContainer = CyberColors.NeonCyan

val DarkTertiary = CyberColors.NeonPurple
val DarkOnTertiary = CyberColors.DarkBackground
val DarkTertiaryContainer = Color(0xFF2D0066)
val DarkOnTertiaryContainer = CyberColors.NeonPurple

val DarkError = CyberColors.NeonRed
val DarkOnError = CyberColors.DarkBackground
val DarkErrorContainer = Color(0xFF660022)
val DarkOnErrorContainer = CyberColors.NeonRed

val DarkBackground = CyberColors.DarkBackground
val DarkOnBackground = CyberColors.TextPrimary

val DarkSurface = CyberColors.DarkSurface
val DarkOnSurface = CyberColors.TextPrimary
val DarkSurfaceVariant = CyberColors.DarkSurfaceVariant
val DarkOnSurfaceVariant = CyberColors.TextSecondary

val DarkOutline = CyberColors.DarkBorder
val DarkOutlineVariant = Color(0xFF484F58)
