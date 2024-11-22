package com.example.messengerx.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.messengerx.ui.theme.MessengerXTheme
import com.example.messengerx.view.GradientBlurButton
import com.example.messengerx.view.MainActivity
import com.example.messengerx.view.registration.RegistrationActivity

val loginGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFF2BE4DC),
        Color(0xFF243484)
    ),
    center = Offset(0.5f, 0.5f),
    radius = Float.POSITIVE_INFINITY
)

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Проверка, вошел ли пользователь
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            navigateToMain()
            return
        }

        setContent {
            MessengerXTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(loginGradient) // Применение градиента для фона
                ) {
                    LoginScreen(onLoginSuccess = {
                        // Сохранение состояния входа
                        sharedPref.edit().putBoolean("is_logged_in", true).apply()
                        navigateToMain()
                    }, onRegisterClick = {
                        navigateToRegistration()
                    })
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // Закрываем все предыдущие активности
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вхід",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логін") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.LightGray,
                containerColor = Color(0x40FFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.LightGray,
                containerColor = Color(0x40FFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        GradientBlurButton(onClick = { onLoginSuccess() }, text = "Увійти")
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = { onRegisterClick() }) {
            Text(text = "Немає аккаунта? Зареєструватися", color = Color.White)
        }
    }
}
