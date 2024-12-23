package com.example.messengerx.view.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.messengerx.api.TokenDataStoreManager
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.MainActivity
import com.example.messengerx.view.registration.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var tokenDataStoreManager: TokenDataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        tokenDataStoreManager = TokenDataStoreManager(this)

        // Проверяем, авторизован ли пользователь
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMain() // Если пользователь уже вошел, переходим в MainActivity
        }

        setContent {
            ThemeMessengerX {
                LoginScreen(
                    onLogin = { email, password -> loginUser(email, password) },
                    onRegisterClick = { navigateToRegistration() }
                )
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = auth.currentUser?.uid ?: ""
                    lifecycleScope.launch {
                        tokenDataStoreManager.saveToken(token)
                        navigateToMain()
                    }
                } else {
                    println("Ошибка входа: ${task.exception?.message}")
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
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .offset(y = 40.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.displayMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            errorMessage = ""
                            onLogin(email, password)
                        } else {
                            errorMessage = "Please fill out all fields"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2681B)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(45.dp)
                ) {
                    Text("Log In", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "If you don't have an account, sign up.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable(onClick = onRegisterClick)
            )
        }
    }
}
