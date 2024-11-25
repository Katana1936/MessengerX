package com.example.messengerx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@Composable
fun ParentComposable() {
    val hazeState = remember { HazeState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .haze(state = hazeState)
    ) {

        BottomNavigationBar(
            hazeState = hazeState,
            onItemSelected = { /* Handle item selection */ }
        )
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomNavigationBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(2) } // Default to "Чаты"

    val items = listOf("Контакты", "Аккаунт", "Чаты", "Настройки")
    val icons = listOf(
        R.drawable.ic_contact,
        R.drawable.ic_account,
        R.drawable.ic_chat,
        R.drawable.ic_settings
    )
    val style = HazeMaterials.regular(MaterialTheme.colorScheme.surface)

    NavigationBar(
        modifier = modifier
            .hazeChild(state = hazeState, style = style)
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.05f),
                        Color.LightGray.copy(alpha = 0.1f)
                    )
                )
            ),
        containerColor = Color.Transparent // Transparent container
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        tint = if (selectedItem == index) Color.Blue else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item,
                        color = if (selectedItem == index) Color.Blue else Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onItemSelected(item)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Blue,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Blue
                )
            )
        }
    }
}




