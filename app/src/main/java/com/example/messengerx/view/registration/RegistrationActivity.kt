package com.example.messengerx.view.registration

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.messengerx.api.TokenDataStoreManager
import com.example.messengerx.ui.theme.ThemeMessengerX
import com.example.messengerx.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RegistrationActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var tokenDataStoreManager: TokenDataStoreManager
    private val firestore = FirebaseFirestore.getInstance()

    // Объявляем isLoading для управления состоянием
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        tokenDataStoreManager = TokenDataStoreManager(this)

        setContent {
            ThemeMessengerX {
                RegistrationScreen { email, password, nickname ->
                    registerUser(email, password, nickname)
                }
            }
        }
    }

    private fun registerUser(email: String, password: String, nickname: String) {
        isLoading = true // Устанавливаем состояние загрузки
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userId = user.uid
                        val userMap = mapOf(
                            "email" to email,
                            "nickname" to nickname,
                            "uid" to userId
                        )

                        // Сохраняем данные пользователя в Firestore
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                lifecycleScope.launch {
                                    tokenDataStoreManager.saveToken(userId) // Сохраняем токен
                                    isLoading = false // Убираем состояние загрузки
                                    navigateToMain() // Переходим на MainActivity
                                }
                            }
                            .addOnFailureListener { e ->
                                println("Ошибка сохранения данных пользователя: ${e.message}")
                                isLoading = false
                            }
                    } else {
                        println("Ошибка: пользователь равен null")
                        isLoading = false
                    }
                } else {
                    println("Ошибка регистрации: ${task.exception?.message}")
                    isLoading = false
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
fun RegistrationScreen(onRegisterSuccess: (String, String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
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
                text = "Register",
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
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
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
                        if (email.isNotBlank() && password.isNotBlank() && nickname.isNotBlank()) {
                            isLoading = true
                            errorMessage = ""
                            onRegisterSuccess(email, password, nickname)
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
                    Text("Register", color = Color.White)
                }
            }
        }
    }
}

