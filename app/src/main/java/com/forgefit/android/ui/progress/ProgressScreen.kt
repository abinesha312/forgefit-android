package com.forgefit.android.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.forgefit.android.ForgeFitApplication
import com.forgefit.android.data.model.PersonalRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(app: ForgeFitApplication) {
    val viewModel = remember {
        ProgressViewModel(
            app.workoutRepository,
            app.database.personalRecordDao(),
            app.database.bodyWeightLogDao()
        )
    }
    
    val records by viewModel.allRecords.collectAsState()
    val weightLogs by viewModel.weightLogs.collectAsState()
    val streak by viewModel.workoutStreak.collectAsState()
    
    var showWeightDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showWeightDialog = true }) {
                Text("+W")
            }
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
                StatsCard(streak = streak, totalPRs = records.size)
            }

            if (records.isNotEmpty()) {
                item {
                    Text("Personal Records", style = MaterialTheme.typography.titleLarge)
                }
                
                items(records.take(20)) { record ->
                    PRCard(record = record)
                }
            }

            if (weightLogs.isNotEmpty()) {
                item {
                    Text("Weight Log", style = MaterialTheme.typography.titleLarge)
                }
                
                items(weightLogs.take(10)) { log ->
                    WeightLogCard(log = log)
                }
            }
        }

        if (showWeightDialog) {
            var weight by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showWeightDialog = false },
                title = { Text("Log Weight") },
                text = {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            weight.toFloatOrNull()?.let {
                                viewModel.addWeightLog(it)
                                showWeightDialog = false
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWeightDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StatsCard(streak: Int, totalPRs: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("$streak", style = MaterialTheme.typography.headlineMedium)
                Text("Day Streak", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("$totalPRs", style = MaterialTheme.typography.headlineMedium)
                Text("Total PRs", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PRCard(record: PersonalRecord) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(record.exerciseName, style = MaterialTheme.typography.titleMedium)
            Text(
                "${record.recordType.name}: ${record.value}kg" + 
                    if (record.reps != null) " × ${record.reps}" else "",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(record.achievedDate)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun WeightLogCard(log: com.forgefit.android.data.model.BodyWeightLog) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${log.weightKg}kg", style = MaterialTheme.typography.titleMedium)
            Text(
                java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                    .format(java.util.Date(log.date)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
