package com.example.messengerx.view.stories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import coil.compose.AsyncImage

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StoriesCarousel(
    viewModel: StoryViewModel,
    userId: String,
    modifier: Modifier = Modifier
) {
    val stories by viewModel.stories.collectAsState()
    val pagerState = rememberPagerState()

    // Загружаем истории при запуске
    LaunchedEffect(Unit) {
        viewModel.fetchStories(userId)
    }

    Box(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        HorizontalPager(
            count = stories.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.7f)
        ) { page ->
            val story = stories[page]
            AsyncImage(
                model = story.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}
