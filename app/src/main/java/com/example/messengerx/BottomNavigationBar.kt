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

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit,
    backgroundColor: Color = Color.White
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
        modifier = modifier.fillMaxWidth(),
        containerColor = backgroundColor
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index

            val iconColor by animateColorAsState(
                targetValue = if (isSelected) Color.Black else Color.Gray
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
                        color = if (isSelected) Color.Black else Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = {
                    selectedItem = index
                    onItemSelected(routes[index])
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }

}
