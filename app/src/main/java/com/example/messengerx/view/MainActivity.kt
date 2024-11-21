package com.example.messengerx.view

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.messengerx.R
import com.example.messengerx.ui.theme.MessengerXTheme
import dev.chrisbanes.haze.HazeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MessengerX)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            MessengerXTheme {
                MainScreen(
                   // homeViewModel = homeViewModel,
//                           walletViewModel = walletViewModel,
                   // profileViewModel = profileViewModel,
                  //  feesViewModel = feesViewModel,
                   // supportViewModel = supportViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
   // homeViewModel: HomeViewModel,
   // profileViewModel: ProfileViewModel,
  //  feesViewModel: FeesViewModel,
   // supportViewModel: SupportViewModel
){
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MessengerXTheme {
        Greeting("Android")
    }
}