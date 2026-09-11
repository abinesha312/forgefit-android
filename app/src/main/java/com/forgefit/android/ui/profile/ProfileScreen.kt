package com.forgefit.android.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.forgefit.android.ForgeFitApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(app: ForgeFitApplication) {
    val viewModel = remember {
        ProfileViewModel(
            app.userRepository,
            app.healthConnectManager
        )
    }
    
    val userProfile by viewModel.userProfile.collectAsState()
    val healthConnectAvailable by viewModel.healthConnectAvailable.collectAsState()
    val healthConnectGranted by viewModel.healthConnectPermissionsGranted.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                userProfile?.let { profile ->
                    ProfileCard(profile = profile)
                }
            }

            item {
                Text("Settings", style = MaterialTheme.typography.titleLarge)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Health Connect", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (healthConnectAvailable) {
                            Text(
                                if (healthConnectGranted) "Connected ✓" else "Not connected",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (healthConnectGranted) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                            
                            if (!healthConnectGranted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { app.healthConnectManager.openHealthConnectSettings() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Grant Permissions")
                                }
                            }
                        } else {
                            Text(
                                "Health Connect not available on this device",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About ForgeFit", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
                        Text("Open source workout tracker", style = MaterialTheme.typography.bodySmall)
                        Text("Not affiliated with Lyfta", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(profile: com.forgefit.android.data.model.UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(profile.displayName, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Age: ${profile.age}", style = MaterialTheme.typography.bodyMedium)
            Text("Height: ${profile.heightCm.toInt()}cm", style = MaterialTheme.typography.bodyMedium)
            Text("Weight: ${profile.weightKg.toInt()}kg", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Goal: ${profile.goal.name.replace("_", " ")}", style = MaterialTheme.typography.bodySmall)
            Text("Experience: ${profile.experience.name}", style = MaterialTheme.typography.bodySmall)
            Text("Days/week: ${profile.trainingDaysPerWeek}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
