package com.example.messengerx.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messengerx.ui.theme.MessengerXTheme
import com.example.messengerx.view.login.LoginActivity
import com.example.messengerx.view.registration.RegistrationActivity

val largeRadialGradient = object : ShaderBrush() {
    override fun createShader(size: Size): android.graphics.Shader {
        val biggerDimension = maxOf(size.height, size.width)
        return RadialGradientShader(
            colors = listOf(
                ComposeColor(0xFF2be4dc),
                ComposeColor(0xFF243484)
            ),
            center = size.center,
            radius = biggerDimension / 2f,
            colorStops = listOf(0f, 0.95f),
            tileMode = TileMode.Clamp
        )
    }
}

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
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
            color = ComposeColor.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "\"MessengerX\"",
            style = MaterialTheme.typography.displayMedium,
            color = ComposeColor.White
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Увійдіть або зареєструйтеся",
            style = MaterialTheme.typography.titleMedium,
            color = ComposeColor.White
        )
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ComposeColor(0xFF004D78),
                contentColor = ComposeColor.White
            ),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = "Увійти",
                style = MaterialTheme.typography.titleMedium,
                color = ComposeColor.White
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "або",
            style = MaterialTheme.typography.titleMedium,
            color = ComposeColor.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ComposeColor(0xFF1E88E5),
                contentColor = ComposeColor.White
            ),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = "Зареєструватись",
                style = MaterialTheme.typography.titleMedium,
                color = ComposeColor.White
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
