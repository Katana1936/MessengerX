package com.example.messengerx.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Цветовые схемы для светлой и тёмной тем
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Основной цвет
    onPrimary = Color.White, // Цвет текста на основном цвете
    primaryContainer = Color(0xFFBB86FC), // Контейнер для основного цвета
    secondary = Color(0xFF03DAC6), // Второстепенный цвет
    onSecondary = Color.Black, // Цвет текста на второстепенном цвете
    background = Color.White, // Фон приложения
    surface = Color.White, // Поверхности
    onBackground = Color.Black, // Текст на фоне
    onSurface = Color.Black // Текст на поверхностях
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // Основной цвет
    onPrimary = Color.Black, // Цвет текста на основном цвете
    primaryContainer = Color(0xFF3700B3), // Контейнер для основного цвета
    secondary = Color(0xFF03DAC6), // Второстепенный цвет
    onSecondary = Color.Black, // Цвет текста на второстепенном цвете
    background = Color(0xFF121212), // Фон приложения
    surface = Color(0xFF121212), // Поверхности
    onBackground = Color.White, // Текст на фоне
    onSurface = Color.White // Текст на поверхностях
)

// Форма углов компонентов
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)


@Composable
fun ThemeMessengerX(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    isTransparent: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val backgroundColor = if (isTransparent) {
        Color.Transparent
    } else {
        colorScheme.background
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.background(backgroundColor)
            ) {
                content()
            }
        }
    )
}

