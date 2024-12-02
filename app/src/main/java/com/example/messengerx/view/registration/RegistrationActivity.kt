package com.example.messengerx.view.registration

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
import com.google.firebase.auth.FirebaseAuth

val registrationGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF2BE4DC), Color(0xFF243484))
)

class RegistrationActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            ThemeMessengerX {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(registrationGradient)
                ) {
                    RegistrationScreen(onRegisterSuccess = { email, password ->
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    sharedPref.edit().putBoolean("is_logged_in", true).apply()
                                    navigateToMain()
                                } else {
                                    // Вывод ошибки или уведомление
                                    println("Ошибка регистрации: ${task.exception?.message}")
                                }
                            }
                    })
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

@Composable
fun RegistrationScreen(onRegisterSuccess: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Реєстрація", style = MaterialTheme.typography.headlineMedium, color = Color.White)
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
        Button(onClick = { onRegisterSuccess(email, password) }) {
            Text("Зареєструватись")
        }
    }
}
