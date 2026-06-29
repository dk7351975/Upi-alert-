package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MpinKeypad(
    onNumberClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    onFingerprintClick: () -> Unit
) {
    val keys = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9),
        listOf(-1, 0, -2) // -1 is fingerprint, -2 is backspace
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (row in keys) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (key in row) {
                    KeypadButton(
                        key = key,
                        onNumberClick = onNumberClick,
                        onBackspaceClick = onBackspaceClick,
                        onFingerprintClick = onFingerprintClick
                    )
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    key: Int,
    onNumberClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    onFingerprintClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(Color.White, CircleShape)
            .clickable {
                when (key) {
                    -1 -> onFingerprintClick()
                    -2 -> onBackspaceClick()
                    else -> onNumberClick(key)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            -1 -> Icon(Icons.Filled.Fingerprint, contentDescription = "Fingerprint", tint = Color(0xFF00C853))
            -2 -> Icon(Icons.Filled.Backspace, contentDescription = "Backspace", tint = Color.DarkGray)
            else -> Text(
                text = key.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun PinIndicator(
    pinLength: Int,
    maxLen: Int = 4
) {
    Row(
        modifier = Modifier.padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (i in 0 until maxLen) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        if (i < pinLength) Color(0xFF00C853) else Color.Transparent,
                        CircleShape
                    )
                    .then(
                        if (i >= pinLength) Modifier.border(2.dp, Color.LightGray, CircleShape)
                        else Modifier
                    )
            )
        }
    }
}
