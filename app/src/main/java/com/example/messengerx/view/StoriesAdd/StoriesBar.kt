package com.example.messengerx.view.StoriesAdd

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.messengerx.R


@Composable
fun StoriesBar(onAddStoryClick: () -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Первый элемент: аватарка текущего пользователя с кнопкой добавления истории
        item {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.Gray) // Замените на изображение пользователя
                        .clickable { onAddStoryClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add), // Иконка добавления
                        contentDescription = "Добавить историю",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp),
                        tint = Color.White
                    )
                }
                Text(text = "Моя история", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Пример других историй
        items(10) { index -> // Замените на реальный список контактов
            StoryAvatar(
                name = "Контакт $index",
                avatarUrl = null // URL аватарки, если есть
            )
        }
    }
}

@Composable
fun StoryAvatar(name: String, avatarUrl: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Gray) // Используйте аватарку из `avatarUrl`
        ) {
            // Placeholder для аватарки
            if (avatarUrl == null) {
                Icon(
                    painter = painterResource(id = R.drawable.avatar_carmen), // Иконка пользователя
                    contentDescription = "Аватар",
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            } else {
                // Загрузите изображение из URL (например, с Coil или Glide)
            }
        }
        Text(text = name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}
