package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.MpinKeypad
import com.example.ui.components.PinIndicator

@Composable
fun SetMpinScreen(onMpinSet: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F9F4)), // Light greenish background
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        // Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF00C853), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.NotificationsActive, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(36.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("UPI Alerts", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF00C853), fontWeight = FontWeight.Bold)
        Text("Set up your app", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = if (isConfirming) "Confirm Security PIN" else "Set Security PIN",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        PinIndicator(pinLength = if (isConfirming) confirmPin.length else pin.length)
        
        errorMsg?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        MpinKeypad(
            onNumberClick = { num ->
                if (isConfirming) {
                    if (confirmPin.length < 4) {
                        confirmPin += num
                        if (confirmPin.length == 4) {
                            if (pin == confirmPin) {
                                onMpinSet(pin)
                            } else {
                                errorMsg = "PINs do not match. Try again."
                                confirmPin = ""
                                pin = ""
                                isConfirming = false
                            }
                        }
                    }
                } else {
                    if (pin.length < 4) {
                        pin += num
                        if (pin.length == 4) {
                            isConfirming = true
                            errorMsg = null
                        }
                    }
                }
            },
            onBackspaceClick = {
                if (isConfirming) {
                    if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                } else {
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                }
            },
            onFingerprintClick = {
                // Not applicable during setup
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 32.dp)) {
            Icon(Icons.Filled.Lock, contentDescription = "Secure", modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("SECURELY PROTECTED", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun UnlockMpinScreen(
    correctMpin: String,
    onUnlockSuccess: () -> Unit,
    onForgotPin: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F9F4)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        // Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF00C853), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.NotificationsActive, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(36.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("UPI Alerts", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF00C853), fontWeight = FontWeight.Bold)
        Text("Unlock App", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text("Enter Security PIN", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        PinIndicator(pinLength = pin.length)
        
        errorMsg?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        MpinKeypad(
            onNumberClick = { num ->
                if (pin.length < 4) {
                    pin += num
                    if (pin.length == 4) {
                        if (pin == correctMpin) {
                            onUnlockSuccess()
                        } else {
                            errorMsg = "Incorrect PIN"
                            pin = ""
                        }
                    }
                }
            },
            onBackspaceClick = {
                if (pin.isNotEmpty()) pin = pin.dropLast(1)
                errorMsg = null
            },
            onFingerprintClick = {
                // Future biometric integration
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        TextButton(onClick = onForgotPin) {
            Text("Forgot PIN?", color = Color(0xFF00C853), fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 32.dp)) {
            Icon(Icons.Filled.Lock, contentDescription = "Secure", modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("SECURELY PROTECTED", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
        }
    }
}
