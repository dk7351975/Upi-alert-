package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val language by viewModel.language.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.updateSettings(settings.copy(soundType = "Custom", customSoundUri = it.toString()))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(com.example.utils.AppStrings.profile, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        // Profile Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(androidx.compose.ui.graphics.Color.Green, androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(com.example.utils.AppStrings.accountActive, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("${com.example.utils.AppStrings.user}: John Doe", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { viewModel.setMpin(null) }) {
                        Text(com.example.utils.AppStrings.changePin)
                    }
                    Button(onClick = { 
                        viewModel.logout()
                        onLogout()
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text(com.example.utils.AppStrings.logout)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Language Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(com.example.utils.AppStrings.language, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = language == "en",
                        onClick = { viewModel.setLanguage("en") }
                    )
                    Text("English")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = language == "hi",
                        onClick = { viewModel.setLanguage("hi") }
                    )
                    Text("हिंदी")
                }
                Text("Note: Restart app to apply language fully", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(com.example.utils.AppStrings.notificationAccess, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(com.example.utils.AppStrings.notificationDesc, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text(com.example.utils.AppStrings.openSettings)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(com.example.utils.AppStrings.soundVibration, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                var expanded by remember { mutableStateOf(false) }
                val sounds = listOf("Default", "Bell", "Cash Register", "Custom")
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = settings.soundType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(com.example.utils.AppStrings.paymentSound) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sounds.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    if (selectionOption == "Custom") {
                                        filePickerLauncher.launch("audio/*")
                                    } else {
                                        viewModel.updateSettings(settings.copy(soundType = selectionOption))
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                if (settings.soundType == "Custom" && settings.customSoundUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Custom sound selected: ${settings.customSoundUri}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(com.example.utils.AppStrings.vibrateOnPayment, style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = settings.vibration,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(vibration = it)) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(com.example.utils.AppStrings.voiceSettings, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                var voiceExpanded by remember { mutableStateOf(false) }
                val voices = listOf("Female", "Male")
                
                ExposedDropdownMenuBox(
                    expanded = voiceExpanded,
                    onExpandedChange = { voiceExpanded = !voiceExpanded }
                ) {
                    OutlinedTextField(
                        value = settings.voiceType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(com.example.utils.AppStrings.customVoiceTitle) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = voiceExpanded,
                        onDismissRequest = { voiceExpanded = false }
                    ) {
                        voices.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    viewModel.updateSettings(settings.copy(voiceType = selectionOption))
                                    voiceExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(com.example.utils.AppStrings.customMessageTitle, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(com.example.utils.AppStrings.customMessageDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = settings.customMessage,
                    onValueChange = { viewModel.updateSettings(settings.copy(customMessage = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
