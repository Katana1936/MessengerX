package com.example.messengerx.view.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var tokenDataStoreManager: TokenDataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        tokenDataStoreManager = TokenDataStoreManager(this)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMain()
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
        if (!isNetworkAvailable()) {
            showToast("Нет подключения к интернету")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(currentUser.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    saveFirebaseToken()
                                    navigateToMain()
                                } else {
                                    showToast("Пользователь не найден в базе.")
                                }
                            }
                    }
                } else {
                    showToast(task.exception?.localizedMessage ?: "Ошибка входа")
                }
            }





    auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    navigateToMain()
                                } else {
                                    showToast("Пользователь не найден в системе.")
                                }
                            }
                    }
                } else {
                    showToast(task.exception?.localizedMessage ?: "Ошибка входа")
                }
            }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
