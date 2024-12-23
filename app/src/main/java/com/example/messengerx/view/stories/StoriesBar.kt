import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.messengerx.api.ApiService
import com.example.messengerx.view.stories.StoryViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun StoriesBar(
    viewModel: StoryViewModel,
    userId: String,
    modifier: Modifier = Modifier,
    onAddStoryClick: () -> Unit
) {
    var selectedStoryUrl by remember { mutableStateOf<String?>(null) }
    val stories by viewModel.stories.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStories(userId)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LazyRow {
            item {
                // Кнопка добавления истории
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = 0.8f))
                        .clickable {
                            onAddStoryClick() // Открытие экрана добавления истории
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                }
            }
            items(stories) { story ->
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray)
                        .clickable { selectedStoryUrl = story.imageUrl }
                ) {
                    AsyncImage(
                        model = story.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    selectedStoryUrl?.let { imageUrl ->
        FullScreenImageDialog(
            imageUrl = imageUrl,
            onDismiss = { selectedStoryUrl = null }
        )
    }
}

@Composable
fun FullScreenImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
