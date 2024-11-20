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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
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
                        .background(largeRadialGradient)
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
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Transparent)
                .alpha(0.7f), // Прозрачность для фона кнопки
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x80FFFFFF), // Полупрозрачный цвет для выделения кнопки
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp), // Закругленные углы для более мягкого внешнего вида
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Увійти",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "або",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Transparent)
                .alpha(0.7f), // Прозрачность для фона кнопки
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x80FFFFFF), // Полупрозрачный цвет для выделения кнопки
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp), // Закругленные углы для более мягкого внешнего вида
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Зареєструватись",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
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
