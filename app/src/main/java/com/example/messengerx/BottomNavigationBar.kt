package com.example.messengerx

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.messengerx.ui.theme.burned_blue
import com.example.messengerx.ui.theme.dark_dark_blue
import com.example.messengerx.ui.theme.light_blue
import com.example.messengerx.ui.theme.white
import dev.chrisbanes.haze.HazeProgressive
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
    onItemSelected: (String) -> Unit = {},
    backgroundColor: Color = Color.Transparent, // Прозрачный фон
    blurRadius: Dp = 25.dp, // Радиус размытия
    tintColors: List<Color> = listOf(Color.White.copy(alpha = 0.1f)), // Полупрозрачный белый оттенок
    noiseFactor: Float = 0.0f,
    progressive: HazeProgressive? = null,
    mask: Brush? = null// Убираем шум для более чистого эффекта
) {
    var selectedItem by remember { mutableStateOf(2) }

    val items = listOf("Контакты", "Аккаунт", "Чаты", "Настройки")

    val selectedIcons = listOf(
        painterResource(id = R.drawable.ic_contact),
        painterResource(id = R.drawable.ic_account),
        painterResource(id = R.drawable.ic_chat),
        painterResource(id = R.drawable.ic_settings)
    )
    val unselectedIcons = listOf(
        painterResource(id = R.drawable.ic_contact),
        painterResource(id = R.drawable.ic_account),
        painterResource(id = R.drawable.ic_chat),
        painterResource(id = R.drawable.ic_settings)
    )

    // Используем CustomHazeStyle
    val hazeStyle = CustomHazeStyle(
        backgroundColor = backgroundColor,
        blurRadius = blurRadius,
        tintColors = tintColors,
        noiseFactor = noiseFactor
    )

    NavigationBar(
        modifier = modifier
            .hazeChild(state = hazeState, style = hazeStyle) // Передаём созданный стиль
            .fillMaxWidth(),
        containerColor = Color.Transparent // Прозрачность, чтобы видно было размытие
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                        tint = if (selectedItem == index) Color.White else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item,
                        color = if (selectedItem == index) Color.White else Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onItemSelected(item)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.White,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

    @Composable
    fun CustomHazeStyle(
        backgroundColor: Color = Color.Transparent, // Прозрачный фон для размытия
        blurRadius: Dp = 25.dp, // Радиус размытия
        tintColors: List<Color> = listOf(Color.White.copy(alpha = 0.1f)), // Полупрозрачный белый оттенок
        noiseFactor: Float = 0.0f // Убираем шум
    ): HazeStyle {
        return HazeStyle(
            backgroundColor = backgroundColor,
            tints = tintColors.map { HazeTint(color = it) },
            blurRadius = blurRadius,
            noiseFactor = noiseFactor,
            fallbackTint = HazeTint(color = tintColors.firstOrNull() ?: Color.Transparent)
        )
    }







