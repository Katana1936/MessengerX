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
    backgroundColor: Color = dark_dark_blue,
    blurRadius: Dp = 200.dp,
    tintColors: List<Color> = listOf(Color.White.copy(alpha = 0.0f)),
    noiseFactor: Float = 0.15f,
    progressive: HazeProgressive? = null,
    mask: Brush? = null
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
    //val hazeStyle = HazeMaterials.ultraThin()
    val hazeStyle = CustomHazeStyle(
        backgroundColor = backgroundColor,
        blurRadius = blurRadius,
        tintColors = tintColors,
        noiseFactor = noiseFactor,
        fallbackTintColor = tintColors.firstOrNull() ?: dark_dark_blue.copy(alpha = 0.0f)
    )
    NavigationBar(
        modifier = modifier
            .hazeChild(state = hazeState) {
                style = hazeStyle
            }
            .fillMaxWidth(),
        containerColor = Color.Transparent
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                        tint = white
                    )
                },
                label = {
                    Text(
                        text = item,
                        color = white,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onItemSelected(item)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = white,
                    unselectedIconColor = white,
                    selectedTextColor = white,
                    indicatorColor = burned_blue,
                    unselectedTextColor = white
                )
            )
        }
    }
}

@Composable
fun CustomHazeStyle(
    backgroundColor: Color = dark_dark_blue,
    blurRadius: Dp = 200.dp,
    tintColors: List<Color> = listOf(dark_dark_blue.copy(alpha = 0.0f)),
    noiseFactor: Float = 0.15f,
    fallbackTintColor: Color = dark_dark_blue.copy(alpha = 0.0f)
): HazeStyle1 {
    return HazeStyle1(
        backgroundColor = backgroundColor,
        tints = tintColors.map { HazeTint(color = it) },
        blurRadius = blurRadius,
        noiseFactor = noiseFactor,
        fallbackTint = HazeTint(color = fallbackTintColor)
    )
}



