package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val payments by viewModel.filteredPayments.collectAsState()
    
    val search by viewModel.senderNameSearch.collectAsState()
    val min by viewModel.minAmountFilter.collectAsState()
    val max by viewModel.maxAmountFilter.collectAsState()
    val apps by viewModel.appFilter.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(com.example.utils.AppStrings.history, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { showFilterSheet = true }) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.senderNameSearch.value = it },
            placeholder = { Text("Search by sender name...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            trailingIcon = {
                if (search.isNotEmpty()) {
                    IconButton(onClick = { viewModel.senderNameSearch.value = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (payments.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(com.example.utils.AppStrings.noPayments, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(payments) { payment ->
                    PaymentItem(payment)
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("Filters", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                var minText by remember { mutableStateOf(min?.toString()?.removeSuffix(".0") ?: "") }
                var maxText by remember { mutableStateOf(max?.toString()?.removeSuffix(".0") ?: "") }
                
                LaunchedEffect(min, max) {
                    if (min == null) minText = ""
                    if (max == null) maxText = ""
                }
                
                OutlinedTextField(
                    value = minText,
                    onValueChange = { 
                        minText = it
                        viewModel.minAmountFilter.value = it.toDoubleOrNull() 
                    },
                    label = { Text("Minimum Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = maxText,
                    onValueChange = { 
                        maxText = it
                        viewModel.maxAmountFilter.value = it.toDoubleOrNull() 
                    },
                    label = { Text("Maximum Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("UPI Apps", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val availableApps = listOf("PhonePe", "Google Pay", "Paytm", "BharatPe", "Amazon Pay", "Other UPI")
                availableApps.forEach { app ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                        val current = apps.toMutableSet()
                        if (current.contains(app)) current.remove(app) else current.add(app)
                        viewModel.appFilter.value = current
                    }.padding(vertical = 4.dp)) {
                        Checkbox(
                            checked = apps.contains(app),
                            onCheckedChange = { isChecked ->
                                val current = apps.toMutableSet()
                                if (isChecked) current.add(app) else current.remove(app)
                                viewModel.appFilter.value = current
                            }
                        )
                        Text(app, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.minAmountFilter.value = null
                        viewModel.maxAmountFilter.value = null
                        viewModel.appFilter.value = emptySet()
                        viewModel.senderNameSearch.value = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All Filters")
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
