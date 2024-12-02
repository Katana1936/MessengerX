package com.example.messengerx.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.MainActivity
import com.example.messengerx.view.registration.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth

val loginGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF2BE4DC), Color(0xFF243484))
)

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            navigateToMain()
            return
        }

        setContent {
            ThemeMessengerX {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(loginGradient)
                ) {
                    LoginScreen(
                        onLogin = { email, password ->
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        sharedPref.edit().putBoolean("is_logged_in", true).apply()
                                        navigateToMain()
                                    } else {
                                        // Обработать ошибку
                                        println("Ошибка входа: ${task.exception?.message}")
                                    }
                                }
                        },
                        onRegisterClick = { navigateToRegistration() }
                    )
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToRegistration() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Вхід", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { onLogin(email, password) }) {
            Text("Увійти")
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = { onRegisterClick() }) {
            Text("Немає аккаунта? Зареєструватися", color = Color.White)
        }
    }
}
