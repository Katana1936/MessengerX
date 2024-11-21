package com.example.messengerx.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messengerx.ui.theme.MessengerXTheme
import com.example.messengerx.view.login.LoginActivity
import com.example.messengerx.view.registration.RegistrationActivity

val largeRadialGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFF2BE4DC),
        Color(0xFF243484)
    ),
    center = Offset(0.5f, 0.5f),
    radius = Float.POSITIVE_INFINITY
)

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerXTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(largeRadialGradient) // Применение градиента для фона
                ) {
                    WelcomeScreen(
                        onLoginClick = { navigateToLogin() },
                        onRegisterClick = { navigateToRegistration() }
                    )
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun GradientBlurButton(onClick: () -> Unit, text: String) {
    Box(
        contentAlignment = Alignment.Center, // Центрирование текста внутри кнопки
        modifier = Modifier
            .fillMaxWidth(0.8f) // Ограничиваем ширину кнопки до 80% от экрана
            .height(50.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(25.dp)) // Закругляем углы самого контейнера Box
    ) {
        // Блюр-слой, который находится под кнопкой и создаёт эффект стекла
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(15.dp) // Размытие для эффекта стекла
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x802BE4DC), // Используем прозрачные цвета для эффекта стекла
                            Color(0x80243484),
                            Color(0x80EEAAEE)
                        )
                    ),
                    shape = RoundedCornerShape(25.dp) // Закругленные края фона кнопки
                )
        )

        // Кнопка с текстом, которая не размывается
        Button(
            onClick = onClick,
            modifier = Modifier
                .matchParentSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Используем прозрачный цвет контейнера
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = text,
                color = Color.White
            )
        }
    }
}

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вітаємо у проекті",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "\"MessengerX\"",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Увійдіть або зареєструйтеся",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(15.dp))

        GradientBlurButton(onClick = onLoginClick, text = "Увійти")
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "або",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))

        GradientBlurButton(onClick = onRegisterClick, text = "Зареєструватись")
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MessengerXTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(largeRadialGradient)
        ) {
            WelcomeScreen(
                onLoginClick = {},
                onRegisterClick = {}
            )
        }
    }
}
