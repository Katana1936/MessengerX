package com.example.messengerx.view

import android.content.Context
import android.content.Intent
import android.graphics.Shader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.login.LoginActivity
import com.example.messengerx.view.registration.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val largeRadialGradient = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val biggerDimension = maxOf(size.height, size.width)
        return RadialGradientShader(
            colors = listOf(Color(0xFF2be4dc), Color(0xFF243484)),
            center = size.center,
            radius = biggerDimension / 2f,
            colorStops = listOf(0f, 0.95f)
        )
    }
}

class WelcomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Проверяем, вошел ли пользователь
        GlobalScope.launch {
            delay(1500) // Задержка для сплэш-экрана
            if (auth.currentUser != null) {
                navigateToMain()
            }
        }

        setContent {
            ThemeMessengerX {
                WelcomeScreen(
                    onLoginClick = { navigateToLogin() },
                    onRegisterClick = { navigateToRegistration() }
                )
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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

        // Кнопка "Увійти" с переходом на LoginActivity
        GradientBlurButton(onClick = onLoginClick, text = "Увійти")
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "або",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Кнопка "Зареєструватись" с переходом на RegistrationActivity
        GradientBlurButton(onClick = onRegisterClick, text = "Зареєструватись")
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ThemeMessengerX {
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
