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
        // Первый элемент: добавление истории
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { onAddStoryClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
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

        // Добавление аватарок пользователей
        items(10) { index -> // Замените на реальный список
            StoryAvatar(
                name = "Пользователь $index",
                avatarResId = R.drawable.avatar // Используем вашу `avatar.xml`
            )
        }
    }
}

@Composable
fun StoryAvatar(name: String, avatarResId: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            Icon(
                painter = painterResource(id = avatarResId),
                contentDescription = "Аватар",
                modifier = Modifier.align(Alignment.Center),
                tint = Color.White
            )
        }
        Text(text = name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

