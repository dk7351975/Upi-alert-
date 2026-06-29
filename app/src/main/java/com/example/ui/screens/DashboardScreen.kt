package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PaymentHistory
import com.example.ui.MainViewModel

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val payments by viewModel.allPayments.collectAsState()
    val todayTotal by viewModel.todayTotal.collectAsState()
    val totalCollection by viewModel.totalCollection.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(com.example.utils.AppStrings.todayCollection, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("₹${todayTotal ?: 0.0}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
            Card(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(com.example.utils.AppStrings.totalCollection, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("₹${totalCollection ?: 0.0}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(com.example.utils.AppStrings.recentPayments, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (payments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(com.example.utils.AppStrings.noPayments)
            }
        } else {
            LazyColumn {
                items(payments.take(10)) { payment ->
                    PaymentItem(payment)
                }
            }
        }
    }
}

@Composable
fun PaymentItem(payment: PaymentHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(payment.sourceApp, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${payment.date} ${payment.time}", style = MaterialTheme.typography.bodySmall)
            }
            Text("₹${payment.amount}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
