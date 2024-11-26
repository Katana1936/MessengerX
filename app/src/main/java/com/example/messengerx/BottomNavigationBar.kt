package com.example.messengerx

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import com.example.messengerx.ui.theme.white
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.HazeStyle as HazeStyle1

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomNavigationBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit = {},
    blurRadius: Dp = 20.dp, // Уменьшаем размытие для лучшего эффекта
    tintColors: List<Color> = listOf(
        Color.White.copy(alpha = 0.1f), // Лёгкий белый градиент
        Color.Cyan.copy(alpha = 0.2f)
    ),
    noiseFactor: Float = 0.1f // Небольшой шум для текстуры
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


    val hazeStyle = HazeStyle1(
        backgroundColor = Color.Transparent,
        blurRadius = blurRadius,
        tints = tintColors.map { HazeTint(color = it) },
        noiseFactor = noiseFactor
    )

    NavigationBar(
        modifier = modifier
            .hazeChild(state = hazeState) { style = hazeStyle } // Применяем Haze
            .fillMaxWidth(),
        containerColor = Color.Transparent // Убираем цвет панели
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                        tint = Color.White
                    )
                },
                label = {
                    Text(
                        text = item,
                        color = Color.White,
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
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    selectedTextColor = Color.White,
                    indicatorColor = Color.Cyan.copy(alpha = 0.3f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}


@Composable
fun CustomHazeStyle(
    backgroundColor: Color = dark_dark_blue,
    blurRadius: Dp = 20.dp,
    tintColors: List<Color> = listOf(
        Color.White.copy(alpha = 0.2f),
        dark_dark_blue.copy(alpha = 0.1f)
    ),
    noiseFactor: Float = 0.05f,
    fallbackTintColor: Color = dark_dark_blue.copy(alpha = 0.1f)
): HazeStyle1 {
    return HazeStyle1(
        backgroundColor = backgroundColor,
        tints = tintColors.map { HazeTint(color = it) },
        blurRadius = blurRadius,
        noiseFactor = noiseFactor,
        fallbackTint = HazeTint(color = fallbackTintColor)
    )
}




