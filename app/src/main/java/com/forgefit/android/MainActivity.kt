package com.forgefit.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.forgefit.android.ui.navigation.ForgeFitNavigation
import com.forgefit.android.ui.navigation.Screen
import com.forgefit.android.ui.theme.ForgeFitTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val app: ForgeFitApplication by lazy {
        application as ForgeFitApplication
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        lifecycleScope.launch {
            val onboardingCompleted = app.userRepository.isOnboardingCompleted()
            val startDestination = if (onboardingCompleted) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }
            
            setContent {
                ForgeFitTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ForgeFitNavigation(
                            app = app,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
