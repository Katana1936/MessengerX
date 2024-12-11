package com.example.messengerx

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomNavigationBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit,
    backgroundColor: Color = Color.Transparent,
    blurRadius: Dp = 25.dp,
    tintColors: List<Color> = listOf(Color.White.copy(alpha = 0.1f)),
    noiseFactor: Float = 0.0f
) {
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf("Чаты", "Контакты", "Аккаунт", "Настройки")
    val routes = listOf("chats", "contacts", "account", "settings")
    val icons = listOf(
        R.drawable.ic_chat,
        R.drawable.ic_contact,
        R.drawable.ic_account,
        R.drawable.ic_settings
    )

    NavigationBar(
        modifier = modifier
            .hazeChild(state = hazeState, style = CustomHazeStyle())
            .fillMaxWidth(),
        containerColor = backgroundColor
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index

            // Анимация цвета и масштаба для иконок
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Gray
            )
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.2f else 1.0f
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        tint = iconColor,
                        modifier = Modifier.scale(scale)
                    )
                },
                label = {
                    Text(
                        text = item,
                        color = if (isSelected) Color.White else Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = {
                    selectedItem = index
                    onItemSelected(routes[index])
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun CustomHazeStyle(
    backgroundColor: Color = Color.Transparent,
    blurRadius: Dp = 30.dp, // Увеличьте радиус для сильного эффекта
    tintColors: List<Color> = listOf(Color.White.copy(alpha = 0.15f)), // Прозрачность
    noiseFactor: Float = 0.05f // Небольшой шум
): HazeStyle {
    return remember {
        HazeStyle(
            backgroundColor = backgroundColor,
            tints = tintColors.map { HazeTint(color = it) },
            blurRadius = blurRadius,
            noiseFactor = noiseFactor,
            fallbackTint = HazeTint(color = tintColors.firstOrNull() ?: Color.Transparent)
        )
    }
}